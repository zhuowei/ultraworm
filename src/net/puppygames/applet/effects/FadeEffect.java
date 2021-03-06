/**
 * 
 */
package net.puppygames.applet.effects;


import com.shavenpuppy.jglib.interpolators.LinearInterpolator;

/**
 * Fades things out
 */
public abstract class FadeEffect extends Effect {

	private final int duration;
	private final int fadeDuration;
	private int tick;
	private boolean fading;
	private int alpha = 255;
	
	public FadeEffect(int duration, int fadeDuration) {
		this.duration = duration;
		this.fadeDuration = fadeDuration;
	}
	
	public void reset() {
		fading = false;
		tick = 0;
		alpha = 255;
	}

	@Override
	public void finish() {
		if (!fading) {
			fading = true;
			tick = 0;
		}
	}
	
	public int getAlpha() {
		return alpha;
	}

	/* (non-Javadoc)
	 * @see net.puppygames.applet.effects.Effect#doTick()
	 */
	@Override
	protected void doTick() {
		tick ++;
		if (fading) {
			alpha = (int) LinearInterpolator.instance.interpolate(255.0f, 0.0f, (float) tick / (float) fadeDuration);
		} else {
			if (tick >= duration) {
				fading = true;
				tick = 0;
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.puppygames.applet.Tickable#isActive()
	 */
	@Override
	public boolean isActive() {
		return !fading || tick < fadeDuration;
	}

}
