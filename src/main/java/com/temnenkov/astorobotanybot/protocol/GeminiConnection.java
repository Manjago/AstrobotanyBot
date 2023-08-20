package com.temnenkov.astorobotanybot.protocol;

import com.temnenkov.astorobotanybot.protocol.exception.ErrorResponseException;
import com.temnenkov.astorobotanybot.protocol.exception.GeminiPanicException;
import com.temnenkov.astorobotanybot.protocol.exception.RedirectedException;
import com.temnenkov.astorobotanybot.protocol.exception.RetryWithInputException;
import lombok.Getter;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;

public class GeminiConnection extends URLConnection {
    private SSLSocket sslSocket;
    private InputStream inputStream;
    private String meta;
    private byte[] content;
    @Getter
    private String contentType = null;

    private static final TrustManager[] TRUST_ALL_CERTS = new TrustManager[]
            {new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs,
                                               String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs,
                                               String authType) {
                }
            }
            };

    protected GeminiConnection(URL url) {
        super(url);
    }

    private static int parseStatus(String line) {
        String[] args = line.split("\\s+", 2);
        try {
            return Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String parseMeta(String line) {
        String[] args = line.split("\\s+", 2);
        if (args.length > 1)
            return args[1];
        else
            return "";
    }

    @Override
    public void connect() throws IOException {
        if (sslSocket != null && sslSocket.isConnected()) {
            return;
        }
        try {
            String host = getURL().getHost();
            int port = getURL().getPort();
            if (port == -1) {
                port = 1965;
            }

            //todo properties file
            final String keyPassphrase = "111111";

            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            //todo properties file
            keyStore.load(new FileInputStream("/home/kirill/del/gemini.pfx"), keyPassphrase.toCharArray());

            String algorithm = KeyManagerFactory.getDefaultAlgorithm();
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(keyStore, keyPassphrase.toCharArray());

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(kmf.getKeyManagers(), TRUST_ALL_CERTS, new java.security.SecureRandom());
            sslSocket = (SSLSocket) sc.getSocketFactory().createSocket(host, port);
            SSLParameters params = new SSLParameters();
            params.setServerNames(Collections.singletonList(new SNIHostName(host)));
            sslSocket.setSSLParameters(params);
            inputStream = sslSocket.getInputStream();
            OutputStream os = sslSocket.getOutputStream();
            PrintStream pos = new PrintStream(os);

            pos.print(getURL().toString());
            pos.print("\r\n");
            pos.flush();

            final StringBuilder sb = new StringBuilder();
            do {
                final int readChar = inputStream.read();
                final char c = (char) readChar;
                if ((readChar == -1) || (c == '\n')){
                    break;
                }
                if (c != '\r') {
                    sb.append(c);
                }
            } while (true);

            final String line = sb.toString();

            final int status = parseStatus(line);
            meta = parseMeta(line);

            //noinspection StatementWithEmptyBody
            if (status >= 20 && status < 30) {
                // Nothing to do -- input stream is now positioned
                //  to read data
            } else {
                sslSocket.close();
                sslSocket = null;
                if (status < 10) {
                    throw new ErrorResponseException(getURL(), status, "Invalid status code in response");
                } else if (status < 20) {
                    throw new RetryWithInputException(getURL(), status == 11, meta);
                } else if (status < 40) {
                    throw new RedirectedException(new URL(meta));
                } else {
                    throw new ErrorResponseException(getURL(), status, meta);
                }
            }
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException | CertificateException |
                 UnrecoverableKeyException e) {
            throw new GeminiPanicException(e);
        }

    }

    @Override
    public Object getContent()
            throws IOException {
        if (content != null) {
            return content;
        }
        try {
            connect();

            contentType = meta;
            content = queryContent();
            sslSocket.close();
            sslSocket = null;

            return content;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private byte[] queryContent() throws IOException {
        try (ByteArrayOutputStream contentBuffer = new ByteArrayOutputStream()) {
            int nRead;
            final byte[] data = new byte[16384];

            try {
                while (sslSocket.isConnected() &&
                        (nRead = inputStream.read(data, 0, data.length)) != -1) {
                    contentBuffer.write(data, 0, nRead);
                }
            } catch (SocketException | SSLException e) {
                throw new GeminiPanicException(e);
            }

            return contentBuffer.toByteArray();
        }
    }

    @Override
    public InputStream getInputStream()
            throws IOException {
        connect();
        return inputStream;
    }


}
