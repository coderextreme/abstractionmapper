package net.coderextreme.assets;

import com.raylib.Camera3D;
import com.raylib.Raylib;
import com.raylib.Vector3;

public class Main {
    public static void main(String args[]) {
        Raylib.initWindow(800, 450, "Demo");
        Raylib.setTargetFPS(60);
        Camera3D camera = new Camera3D()
                .position(new Vector3().x(18).y(16).z(18))
                .target(new Vector3())
                .up(new Vector3().x(0).y(1).z(0))
                .fovy(45).projection(Raylib.CameraProjection.CAMERA_PERSPECTIVE);
        // Add this line only if Raylib version < 4.5:
        // SetCameraMode(camera, CAMERA_ORBITAL);

        while (!Raylib.windowShouldClose()) {
            Raylib.updateCamera(camera, Raylib.CameraMode.CAMERA_ORBITAL);
            Raylib.beginDrawing();
            Raylib.clearBackground(Raylib.RAYWHITE);
            Raylib.beginMode3D(camera);
            Raylib.drawGrid(20, 1.0f);
            Raylib.endMode3D();
            Raylib.drawText("Hello world", 190, 200, 20, Raylib.VIOLET);
            Raylib.drawFPS(20, 20);
            Raylib.endDrawing();
        }
        Raylib.closeWindow();
    }
}
