package com.temnenkov.astorobotanybot.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;


//inspired by kotlin.Result
public class Result<T> {
    private final Object value;

    private Result(Object value) {
        this.value = value;
    }

    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull Result<T> success(T value) {
        return new Result<>(value);
    }

    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull Result<T> failure(Throwable exception) {
        return new Result<>(new Failure(exception));
    }

    public static <T> @NotNull Result<T> runCatching(@NotNull Callable<T> callable) {
        try {
            return Result.success(callable.call());
        } catch (Throwable t) {
            return Result.failure(t);
        }
    }

    public boolean isSuccess() {
        return !(value instanceof Failure);
    }

    public boolean isFailure() {
        return value instanceof Failure;
    }

    public T getOrNull() {
        if (value instanceof Failure) {
            return null;
        } else {
            //noinspection unchecked
            return (T) value;
        }
    }

    public Throwable exceptionOrNull() {
        if (value instanceof Failure) {
            return ((Failure) value).exception;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        if (value instanceof Failure) {
            return value.toString();
        } else {
            return "Success(" + value + ")";
        }
    }

    private record Failure(Throwable exception) {
    }
}
