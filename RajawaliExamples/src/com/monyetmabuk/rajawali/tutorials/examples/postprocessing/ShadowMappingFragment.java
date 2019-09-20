package com.monyetmabuk.rajawali.tutorials.examples.postprocessing;

import rajawali.Object3D;
import rajawali.animation.Animation.RepeatMode;
import rajawali.animation.TranslateAnimation3D;
import rajawali.lights.DirectionalLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.math.vector.Vector3;
import rajawali.math.vector.Vector3.Axis;
import rajawali.postprocessing.PostProcessingManager;
import rajawali.postprocessing.effects.ShadowEffect;
import rajawali.primitives.Cube;
import rajawali.primitives.Plane;
import android.content.Context;
import android.graphics.Color;

import com.monyetmabuk.rajawali.tutorials.examples.AExampleFragment;

public class ShadowMappingFragment extends AExampleFragment {

	@Override
	protected AExampleRenderer createRenderer() {
		return new ShadowMappingRenderer(getActivity());
	}

	private final class ShadowMappingRenderer extends AExampleRenderer {
		private PostProcessingManager mPostProcessingManager;
		private Object3D mEmpty;
		private DirectionalLight mLight;
		
		public ShadowMappingRenderer(Context context) {
			super(context);
		}
		
		public void initScene() {
			mLight = new DirectionalLight();
			mLight.setDirection(1, -1, -1);
			mLight.setPower(1.5f);
			getCurrentScene().addLight(mLight);
			getCurrentCamera().setFarPlane(50);		
			getCurrentCamera().setPosition(5, 15, 30);
			getCurrentCamera().setLookAt(0, 0, 0);

			Material planeMaterial = new Material();
			planeMaterial.enableLighting(true);
			planeMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
			
			Plane plane = new Plane(Axis.Y);
			plane.setScale(10);
			plane.setMaterial(planeMaterial);
			plane.setColor(Color.GREEN);
			getCurrentScene().addChild(plane);		
			
			Material sphereMaterial = new Material();
			sphereMaterial.enableLighting(true);
			sphereMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
			
			for(int z=0; z<10; z++) {
				for(int x=0; x<10; x++) {
					Cube cube = new Cube(.7f);
					cube.setMaterial(sphereMaterial);
					cube.setColor(Color.rgb(100 + 10 * x, 0, 0));
					cube.setPosition(-4.5f + x, 5, -4.5f + z);
					
					getCurrentScene().addChild(cube);
				}
			}
			
			Material cubeMaterial = new Material();
			cubeMaterial.enableLighting(true);
			cubeMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
			
			Cube cube = new Cube(2);
			cube.setMaterial(cubeMaterial);
			cube.setColor(Color.GRAY);
			cube.setY(1.5f);
			getCurrentScene().addChild(cube);
			
			mEmpty = new Object3D();
			TranslateAnimation3D anim = new TranslateAnimation3D(new Vector3(5, -5, -4), new Vector3(-5, -5, 4));
			anim.setDurationMilliseconds(20000);
			anim.setRepeatMode(RepeatMode.REVERSE_INFINITE);
			anim.setTransformable3D(mEmpty);
			getCurrentScene().registerAnimation(anim);
			anim.play();
			
			mPostProcessingManager = new PostProcessingManager(this);
			ShadowEffect shadowEffect = new ShadowEffect(getCurrentScene(), getCurrentCamera(), mLight, 2048);
			shadowEffect.setShadowInfluence(.5f);
			mPostProcessingManager.addEffect(shadowEffect);
			shadowEffect.setRenderToScreen(true);
		}
		
		@Override
		public void onRender(final double deltaTime) {
			mLight.setLookAt(mEmpty.getPosition());
			mPostProcessingManager.render(deltaTime);
		}
	}
}
