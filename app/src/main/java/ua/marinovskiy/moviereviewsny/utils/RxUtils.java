package ua.marinovskiy.moviereviewsny.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.concurrent.ExecutionException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Alex on 02.03.2016.
 */
public class RxUtils {

    public static <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Pair<Palette, Bitmap>> generatePalette(@NonNull String url, @NonNull Context context) {
        return Observable.create(
                new Observable.OnSubscribe<Pair<Palette, Bitmap>>() {
                    @Override
                    public void call(Subscriber<? super Pair<Palette, Bitmap>> subscriber) {
                        try {
                            Bitmap bitmap = Glide.with(context)
                                    .load(url)
                                    .asBitmap()
                                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                    .into(75, 75)
                                    .get();
                            if (bitmap != null) {
                                Palette palette = new Palette.Builder(bitmap)
                                        .generate();
                                subscriber.onNext(new Pair<>(palette, bitmap));
                                subscriber.onCompleted();
                            } else {
                                subscriber.onError(new Throwable("Can't generate palette"));
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            subscriber.onError(e);
                        }
                    }
                }
        ).compose(applySchedulers());
    }


}
