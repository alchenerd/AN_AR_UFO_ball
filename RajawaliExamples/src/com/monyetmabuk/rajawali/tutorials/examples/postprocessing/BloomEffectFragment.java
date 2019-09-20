package com.monyetmabuk.rajawali.tutorials.examples.postprocessing;

import java.util.Random;

import rajawali.animation.Animation.RepeatMode;
import rajawali.animation.RotateOnAxisAnimation;
import rajawali.lights.DirectionalLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.math.vector.Vector3;
import rajawali.postprocessing.PostProcessingManager;
import rajawali.postprocessing.effects.BloomEffect;
import rajawali.postprocessing.passes.BlendPass.BlendMode;
import rajawali.postprocessing.passes.RenderPass;
import rajawali.primitives.Cube;
import rajawali.util.RajLog;
import android.content.Context;
import android.graphics.Color;

import com.monyetmabuk.rajawali.tutorials.examples.AExampleFragment;

public class BloomEffectFragment extends AExampleFragment {

	@Override
	protected AExampleRenderer createRenderer() {
		return new BloomEffectRenderer(getActivity());
	}

	private final class BloomEffectRenderer extends AExampleRenderer {
		private PostProcessingManager mEffects;

		public BloomEffectRenderer(Context context) {
			super(context);
		}

		public void initScene() {
			DirectionalLight light = new DirectionalLight();
			light.setPower(1);
			getCurrentScene().setBackgroundColor(Color.BLACK);
			getCurrentScene().addLight(light);

			//
			// -- Create a material for all cubes
			//
			
			Material material = new Material();
			material.enableLighting(true);
			material.setDiffuseMethod(new DiffuseMethod.Lambert());

			getCurrentCamera().setZ(10);

			Random random = new Random();
			
			//
			// -- Generate cubes with random x, y, z
			//

			for (int i = 0; i < 20; i++) {
				Cube cube = new Cube(1);
				cube.setPosition(-5 + random.nextFloat() * 10,
						-5 + random.nextFloat() * 10, random.nextFloat() * -10);
				cube.setMaterial(material);
				cube.setColor(0x666666 + random.nextInt(0x999999));
				getCurrentScene().addChild(cube);

				Vector3 randomAxis = new Vector3(random.nextFloat(),
						random.nextFloat(), random.nextFloat());
				randomAxis.normalize();

				RotateOnAxisAnimation anim = new RotateOnAxisAnimation(randomAxis,
						360);
				anim.setTransformable3D(cube);
				anim.setDurationMilliseconds(3000 + (int) (random.nextDouble() * 5000));
				anim.setRepeatMode(RepeatMode.INFINITE);
				getCurrentScene().registerAnimation(anim);
				anim.play();
			}
			
			//
			// -- Create a post processing manager. We can add multiple passes to this.
			//

			mEffects = new PostProcessingManager(this);
			RenderPass renderPass = new RenderPass(getCurrentScene(), getCurrentCamera(), 0);
			mEffects.addPass(renderPass);
			
			BloomEffect bloomEffect = new BloomEffect(getCurrentScene(), getCurrentCamera(), mViewportWidth, mViewportHeight, 0x111111, 0xffffff, BlendMode.SCREEN);
			mEffects.addEffect(bloomEffect);

			bloomEffect.setRenderToScreen(true);
			
			RajLog.i("Viewport: " + mViewportWidth + ", " + mViewportHeight);
		}

		@Override
		public void onRender(final double deltaTime) {
			mEffects.render(deltaTime);
		}
	}
}
