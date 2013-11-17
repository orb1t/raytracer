/*===========================================================================
COPYRIGHT 2013 Vinícius G. Mendonça ALL RIGHTS RESERVED.

This software cannot be copied, stored, distributed without
Vinícius G. Mendonça prior authorization.

This file was made available on https://github.com/ViniGodoy and it
is free to be redistributed or used under Creative Commons license 2.5 br:
http://creativecommons.org/licenses/by-sa/2.5/br/
============================================================================*/

package br.com.vinigodoy.raytracer.camera;

import br.com.vinigodoy.raytracer.math.Ray;
import br.com.vinigodoy.raytracer.math.Vector2;
import br.com.vinigodoy.raytracer.math.Vector3;
import br.com.vinigodoy.raytracer.scene.ViewPlane;
import br.com.vinigodoy.raytracer.scene.World;
import br.com.vinigodoy.raytracer.utility.UVW;

import static br.com.vinigodoy.raytracer.scene.order.PixelArray.Pixel;

/**
 * Represents a pinhole perspective camera. The camera can focus all objects within the camera lens.
 */
public class PinholeCamera extends AbstractCamera {
    private float viewPlaneDistance;
    private float zoom;

    public PinholeCamera(Vector3 eyePosition, Vector3 lookPoint, Vector3 upDirection, float viewPlaneDistance) {
        super(eyePosition, lookPoint, upDirection);
        this.viewPlaneDistance = viewPlaneDistance;
        this.zoom = 1.0f;
    }

    public float getViewPlaneDistance() {
        return viewPlaneDistance;
    }

    public float getZoom() {
        return zoom;
    }

    public void setViewPlaneDistance(float viewPlaneDistance) {
        this.viewPlaneDistance = viewPlaneDistance;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    @Override
    public void render(World w, ViewPlane vp) {
        UVW uvw = computeUVW();
        float s = vp.getS() / zoom;

        for (Pixel pixel : vp.getPixels()) {
            int c = pixel.getX();
            int r = pixel.getY();

            Vector3 L = new Vector3();

            for (int i = 0; i < vp.getSampler().getNumSamples(); i++) {
                Vector2 sp = vp.getSampler().nextSampleSquare();

                Vector2 pp = new Vector2(
                        s * (c - 0.5f * vp.getHRes() + sp.getX()),
                        s * (r - 0.5f * vp.getVRes() + sp.getY()));
                Ray ray = new Ray(eye, getDirection(pp, uvw));
                L.add(w.getTracer().trace(w, ray, 0));
            }

            L.divide(vp.getSampler().getNumSamples()).multiply(exposureTime);
            drawPixel(w, vp, c, r, L);
        }
    }

    public Vector3 getDirection(Vector2 p, UVW uvw) {
        return uvw.transform(p, -viewPlaneDistance);
    }
}
