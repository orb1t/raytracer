/*===========================================================================
COPYRIGHT 2013 Vinícius G. Mendonça ALL RIGHTS RESERVED.

This software cannot be copied, stored, distributed without
Vinícius G. Mendonça prior authorization.

This file was made available on https://github.com/ViniGodoy and it
is free to be redistributed or used under Creative Commons license 2.5 br:
http://creativecommons.org/licenses/by-sa/2.5/br/
============================================================================*/

package br.com.vinigodoy.raytracer.material;

import br.com.vinigodoy.raytracer.brdf.Lambertian;
import br.com.vinigodoy.raytracer.light.Light;
import br.com.vinigodoy.raytracer.math.Ray;
import br.com.vinigodoy.raytracer.math.Vector3;
import br.com.vinigodoy.raytracer.utility.ShadeRec;

import static br.com.vinigodoy.raytracer.math.Vector3.mul;
import static br.com.vinigodoy.raytracer.math.Vector3.negate;

public abstract class AbstractMaterial implements Material {
    protected Lambertian ambient;

    public AbstractMaterial(float ka, Vector3 color) {
        this.ambient = new Lambertian(ka, color);
    }

    @Override
    public Vector3 shade(ShadeRec sr) {
        Vector3 wo = negate(sr.ray.getDirection());
        Vector3 L = mul(ambient.rho(sr, wo), sr.world.getAmbientLight().L(sr));
        for (Light light : sr.world.getLights()) {
            Vector3 wi = light.getDirection(sr);

            float ndotwi = sr.normal.dot(wi);
            if (ndotwi > 0.0f) {
                boolean inShadow = false;
                if (light.castShadows()) {
                    Ray shadowRay = new Ray(sr.localHitPoint, wi);
                    inShadow = light.inShadow(shadowRay, sr);
                }

                if (!inShadow) {
                    L.add(processLight(sr, wo, light, wi, ndotwi));
                }
            }
        }
        return L;
    }

    public void setKa(float k) {
        ambient.setKd(k);
    }

    public void setCd(Vector3 color) {
        ambient.setCd(color);
    }

    public float getKa() {
        return ambient.getKd();
    }

    public Vector3 getCd() {
        return ambient.getCd();
    }


    public abstract Material clone();

    protected abstract Vector3 processLight(ShadeRec sr, Vector3 wo, Light light, Vector3 wi, float ndotwi);
}