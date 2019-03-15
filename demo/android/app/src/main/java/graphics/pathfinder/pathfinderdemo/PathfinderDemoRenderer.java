package graphics.pathfinder.pathfinderdemo;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;
import javax.microedition.khronos.egl.EGLConfig;

public class PathfinderDemoRenderer extends Object implements GvrView.Renderer {
    private PathfinderActivity mActivity;
    private boolean mInitialized;
    private boolean mInVRMode;

    private static native void init(PathfinderDemoResourceLoader resourceLoader,
                                    int width,
                                    int height);

    private static native int prepareFrame();

    private static native void drawScene(int sceneIndex);

    private static native void finishDrawingFrame();

    public static native void pushWindowResizedEvent(int width, int height);

    public static native void pushMouseDownEvent(int x, int y);

    public static native void pushMouseDraggedEvent(int x, int y);

    public static native void pushLookEvent(float pitch, float yaw);

    static {
        System.loadLibrary("pathfinder_android_demo");
    }

    PathfinderDemoRenderer(PathfinderActivity activity) {
        super();
        mActivity = activity;
        mInitialized = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDrawFrame(HeadTransform headTransform, Eye leftEye, Eye rightEye) {
        boolean inVR = prepareFrame() > 1;
        if (inVR != mInVRMode) {
            mInVRMode = inVR;
            try {
                mActivity.setVrModeEnabled(mInVRMode, mActivity.mVRListenerComponentName);
            } catch (PackageManager.NameNotFoundException exception) {
                throw new RuntimeException(exception);
            }
        }

        for (int sceneIndex = 0; sceneIndex < (inVR ? 2 : 1); sceneIndex++)
            drawScene(sceneIndex);
        finishDrawingFrame();
    }

    @Override
    public void onFinishFrame(Viewport viewport) {

    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        if (!mInitialized) {
            init(new PathfinderDemoResourceLoader(mActivity.getAssets()), width, height);
            mInitialized = true;
        } else {
            pushWindowResizedEvent(width, height);
        }

    }

    @Override
    public void onSurfaceCreated(EGLConfig config) {

    }

    @Override
    public void onRendererShutdown() {

    }
}