package com.loopj.android.image;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 *    PhotoView SmartImageView 组合实现 网络请求图片 + 缩放功能
 *    
 *    PhotoView  http://www.boyunjian.com/javasrc/com.github.chrisbanes.photoview/library/1.2/_/uk/co/senab/photoview/ScrollerProxy.java
 * @author Zheng
 *
 */
public class SmartPhotoView extends ImageView {
    private static final int LOADING_THREADS = 4;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(LOADING_THREADS);

    private SmartImageTask currentTask;

    //缩放
    private final PhotoViewAttacher mAttacher;
    
    public SmartPhotoView(Context context) {
        super(context);
        super.setScaleType(ScaleType.MATRIX);
        mAttacher = new PhotoViewAttacher(this);
    }

    public SmartPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setScaleType(ScaleType.MATRIX);
        mAttacher = new PhotoViewAttacher(this);
    }

    public SmartPhotoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setScaleType(ScaleType.MATRIX);
        mAttacher = new PhotoViewAttacher(this);
    }


    // Helpers to set image by URL
    public void setImageUrl(String url) {
        setImage(new WebImage(url));
    }

    public void setImageUrl(String url, SmartImageTask.OnCompleteListener completeListener) {
        setImage(new WebImage(url), completeListener);
    }

    public void setImageUrl(String url, final Integer fallbackResource) {
        setImage(new WebImage(url), fallbackResource);
    }

    public void setImageUrl(String url, final Integer fallbackResource, SmartImageTask.OnCompleteListener completeListener) {
        setImage(new WebImage(url), fallbackResource, completeListener);
    }

    public void setImageUrl(String url, final Integer fallbackResource, final Integer loadingResource) {
        setImage(new WebImage(url), fallbackResource, loadingResource);
    }

    public void setImageUrl(String url, final Integer fallbackResource, final Integer loadingResource, SmartImageTask.OnCompleteListener completeListener) {
        setImage(new WebImage(url), fallbackResource, loadingResource, completeListener);
    }


    // Helpers to set image by contact address book id
    public void setImageContact(long contactId) {
        setImage(new ContactImage(contactId));
    }

    public void setImageContact(long contactId, final Integer fallbackResource) {
        setImage(new ContactImage(contactId), fallbackResource);
    }

    public void setImageContact(long contactId, final Integer fallbackResource, final Integer loadingResource) {
        setImage(new ContactImage(contactId), fallbackResource, fallbackResource);
    }


    // Set image using SmartImage object
    public void setImage(final SmartImage image) {
        setImage(image, null, null, null);
    }

    public void setImage(final SmartImage image, final SmartImageTask.OnCompleteListener completeListener) {
        setImage(image, null, null, completeListener);
    }

    public void setImage(final SmartImage image, final Integer fallbackResource) {
        setImage(image, fallbackResource, fallbackResource, null);
    }

    public void setImage(final SmartImage image, final Integer fallbackResource, SmartImageTask.OnCompleteListener completeListener) {
        setImage(image, fallbackResource, fallbackResource, completeListener);
    }

    public void setImage(final SmartImage image, final Integer fallbackResource, final Integer loadingResource) {
        setImage(image, fallbackResource, loadingResource, null);
    }

    public void setImage(final SmartImage image, final Integer fallbackResource, final Integer loadingResource, final SmartImageTask.OnCompleteListener completeListener) {
        // Set a loading resource
        if(loadingResource != null){
            setImageResource(loadingResource);
        }

        // Cancel any existing tasks for this image view
        if(currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }

        // Set up the new task
        currentTask = new SmartImageTask(getContext(), image);
        currentTask.setOnCompleteHandler(new SmartImageTask.OnCompleteHandler() {
            @Override
            public void onComplete(Bitmap bitmap) {
                if(bitmap != null) {
                    setImageBitmap(bitmap);
                } else {
                    // Set fallback resource
                    if(fallbackResource != null) {
                        setImageResource(fallbackResource);
                    }
                }

                if(completeListener != null){
                    completeListener.onComplete();
                }
            }
        });

        // Run the task in a threadpool
        threadPool.execute(currentTask);
    }

    public static void cancelAllTasks() {
        threadPool.shutdownNow();
        threadPool = Executors.newFixedThreadPool(LOADING_THREADS);
    }
    
    
    //缩放功能 PhotoView
    
    
    /**
     * Returns true if the PhotoView is set to allow zooming of Photos.
     * 
     * @return true if the PhotoView allows zooming.
     */
    public boolean canZoom() {
        return mAttacher.canZoom();
    }
 
    /**
     * Gets the Display Rectangle of the currently displayed Drawable. The
     * Rectangle is relative to this View and includes all scaling and
     * translations.
     * 
     * @return - RectF of Displayed Drawable
     */
    public RectF getDisplayRect() {
        return mAttacher.getDisplayRect();
    }
 
    /**
     * Returns the current scale value
     * 
     * @return float - current scale value
     */
    public float getScale() {
        return mAttacher.getScale();
    }
     
    @Override
    public ScaleType getScaleType() {
    	return ScaleType.MATRIX;
//    	if(mAttacher==null){
//    		return ScaleType.MATRIX;
//    	}
//        return mAttacher.getScaleType();
    }
    
    @Override
    public void setImageResource(int resId) {
    	super.setImageResource(resId);
    	if(mAttacher != null) {
            mAttacher.update();
        }
    }
    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if(mAttacher != null) {
            mAttacher.update();
        }
    }
 
    /**
     * Register a callback to be invoked when the Matrix has changed for this
     * View. An example would be the user panning or scaling the Photo.
     * 
     * @param listener
     *            - Listener to be registered.
     */
    public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
        mAttacher.setOnMatrixChangeListener(listener);
    }
 
    /**
     * Register a callback to be invoked when the Photo displayed by this View
     * is tapped with a single tap.
     * 
     * @param listener
     *            - Listener to be registered.
     */
    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        mAttacher.setOnPhotoTapListener(listener);
    }
     
    @Override
    public void setScaleType(ScaleType scaleType) {
        mAttacher.setScaleType(scaleType);
    }
 
    /**
     * Allows you to enable/disable the zoom functionality on the ImageView.
     * When disable the ImageView reverts to using the FIT_CENTER matrix.
     * 
     * @param zoomable
     *            - Whether the zoom functionality is enabled.
     */
    public void setZoomable(boolean zoomable) {
        mAttacher.setZoomable(zoomable);
    }
 
    /**
     * Zooms to the specified scale, around the focal point given.
     * 
     * @param scale
     *            - Scale to zoom to
     * @param focalX
     *            - X Focus Point
     * @param focalY
     *            - Y Focus Point
     */
    public void zoomTo(float scale, float focalX, float focalY) {
        mAttacher.setScale(scale, focalX, focalY, true);
        //.zoomTo(scale, focalX, focalY);
    }
    
    
    
}