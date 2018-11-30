package com.mibarim.main.ui;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;

/**
 * Created by Hamed on 2/2/2017.
 */

@SuppressWarnings("unused")
public class ImageBehavior extends CoordinatorLayout.Behavior<BootstrapCircleThumbnail> {

    private boolean isAnimatingOut;
    Rect mTmpRect;
    private FastOutSlowInInterpolator fastOutSlowInInterpolator = new FastOutSlowInInterpolator();

    public ImageBehavior() {
    }

    public ImageBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, BootstrapCircleThumbnail child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, BootstrapCircleThumbnail child, View dependency) {
        if (dependency instanceof AppBarLayout) {
            AppBarLayout appBarLayout = (AppBarLayout) dependency;
            if(this.mTmpRect == null) {
                this.mTmpRect = new Rect();
            }

            Rect rect = this.mTmpRect;
            ViewGroupUtils.getDescendantRect(parent, appBarLayout, rect);
            if(rect.bottom <= child.getHeight()*1.5) {
                if (!this.isAnimatingOut && child.getVisibility() == View.VISIBLE) {
                    this.animateOut(child);
                }
            } else if (child.getVisibility() != View.VISIBLE) {
                this.animateIn(child);
            }
        }

        return false;
    }

    private int getMinimumHeightForContext(AppBarLayout appBarLayout) {
        int minHeight =  ViewCompat.getMinimumHeight(appBarLayout);
        if (minHeight != 0) {
            return minHeight * 2;
        } else {
            int childCount = appBarLayout.getChildCount();
            return childCount >= 1 ? ViewCompat.getMinimumHeight(appBarLayout.getChildAt(childCount - 1)) * 2 : 0;
        }
    }

    private void animateIn(View view) {
        view.setVisibility(View.VISIBLE);
        ViewCompat.animate(view)
                .scaleX(1.0F)
                .scaleY(1.0F)
                .alpha(1.0F)
                .setInterpolator(fastOutSlowInInterpolator)
                .withLayer()
                .setListener((ViewPropertyAnimatorListener) null).start();
    }

    private void animateOut(final View view) {
        ViewCompat.animate(view)
                .scaleX(0.0F)
                .scaleY(0.0F)
                .alpha(0.0F)
                .setInterpolator(fastOutSlowInInterpolator)
                .withLayer()
                .setListener(new ViewPropertyAnimatorListener() {
                    public void onAnimationStart(View view) {
                        ImageBehavior.this.isAnimatingOut = true;
                    }

                    public void onAnimationCancel(View view) {
                        ImageBehavior.this.isAnimatingOut = false;
                    }

                    public void onAnimationEnd(View view) {
                        ImageBehavior.this.isAnimatingOut = false;
                        view.setVisibility(View.GONE);
                    }
                }).start();
    }

    private static class ViewGroupUtils {
        private static final ViewGroupUtils.ViewGroupUtilsImpl IMPL;

        ViewGroupUtils() {
        }

        static void offsetDescendantRect(ViewGroup parent, View descendant, Rect rect) {
            IMPL.offsetDescendantRect(parent, descendant, rect);
        }

        static void getDescendantRect(ViewGroup parent, View descendant, Rect out) {
            out.set(0, 0, descendant.getWidth(), descendant.getHeight());
            offsetDescendantRect(parent, descendant, out);
        }

        static {
            int version = Build.VERSION.SDK_INT;
            if(version >= 11) {
                IMPL = new ViewGroupUtils.ViewGroupUtilsImplHoneycomb();
            } else {
                IMPL = new ViewGroupUtils.ViewGroupUtilsImplBase();
            }

        }

        private static class ViewGroupUtilsImplHoneycomb implements ViewGroupUtils.ViewGroupUtilsImpl {
            private ViewGroupUtilsImplHoneycomb() {
            }

            public void offsetDescendantRect(ViewGroup parent, View child, Rect rect) {
                ViewGroupUtilsHoneycomb.offsetDescendantRect(parent, child, rect);
            }
        }

        private static class ViewGroupUtilsImplBase implements ViewGroupUtils.ViewGroupUtilsImpl {
            private ViewGroupUtilsImplBase() {
            }

            public void offsetDescendantRect(ViewGroup parent, View child, Rect rect) {
                parent.offsetDescendantRectToMyCoords(child, rect);
            }
        }

        private interface ViewGroupUtilsImpl {
            void offsetDescendantRect(ViewGroup var1, View var2, Rect var3);
        }
    }

    private static class ViewGroupUtilsHoneycomb {
        private static final ThreadLocal<Matrix> sMatrix = new ThreadLocal();
        private static final ThreadLocal<RectF> sRectF = new ThreadLocal();
        private static final Matrix IDENTITY = new Matrix();

        ViewGroupUtilsHoneycomb() {
        }

        public static void offsetDescendantRect(ViewGroup group, View child, Rect rect) {
            Matrix m = (Matrix)sMatrix.get();
            if(m == null) {
                m = new Matrix();
                sMatrix.set(m);
            } else {
                m.set(IDENTITY);
            }

            offsetDescendantMatrix(group, child, m);
            RectF rectF = (RectF)sRectF.get();
            if(rectF == null) {
                rectF = new RectF();
            }

            rectF.set(rect);
            m.mapRect(rectF);
            rect.set((int)(rectF.left + 0.5F), (int)(rectF.top + 0.5F), (int)(rectF.right + 0.5F), (int)(rectF.bottom + 0.5F));
        }

        static void offsetDescendantMatrix(ViewParent target, View view, Matrix m) {
            ViewParent parent = view.getParent();
            if(parent instanceof View && parent != target) {
                View vp = (View)parent;
                offsetDescendantMatrix(target, vp, m);
                m.preTranslate((float)(-vp.getScrollX()), (float)(-vp.getScrollY()));
            }

            m.preTranslate((float)view.getLeft(), (float)view.getTop());
            if(!view.getMatrix().isIdentity()) {
                m.preConcat(view.getMatrix());
            }

        }
    }
}
