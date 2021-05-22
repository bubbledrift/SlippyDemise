package engine.game.components.animation;

import engine.game.components.Component;
import engine.game.systems.SystemFlag;

import java.util.ArrayList;
import java.util.List;

public class AnimationComponent extends Component {

    /*
    Test Animation Component. If this works well will replace the other Animation Component

    Idea here is to have a pointer to the fields that need to be animated.
    This way a single animation component does the clock timings and can adjust the positions, currentFrames, sizes etc
    of an arbitrary amount of other objects concurrently.

    This should allow:
    - animation of collision shapes (hit boxes)
    - animation of sprites (as before, but synced with other animations)
    - animation of general fields (boolean, Vec2d, int, String, etc)
    - syncing animation across many game objects (tiles) **this may be harder to make work game side

     */

    private class AnimationSequence<T>{
        public AnimationField field;
        public T[] sequence;
        public AnimationSequence(AnimationField field, T[] sequence){
            this.field = field;
            this.sequence = sequence;
        }
    }

    protected int steps;

    protected double frameDuration;

    protected long currentTime = 0;
    protected int currentStep = 0;

    private List<AnimationSequence> animationSequenceList;

    public boolean justFinished;

    public AnimationComponent(int steps, double frameDuration){
        this.steps = steps;
        this.frameDuration = frameDuration * 1000000000;
        this.animationSequenceList = new ArrayList<AnimationSequence>();
    }

    /**
     * Adds a new animation sequence to this Animation Component
     * @param field The field who's value is updated
     * @param sequence The sequence of values to be assigned to the given field at steps of the animation.
     *                 If null then the current animation step (0 indexed) is used as the setpoint.
     */
    public void addAnimationSequence(AnimationField field, Object[] sequence){
        assert(sequence.length == this.steps);
        this.animationSequenceList.add(new AnimationSequence(field, sequence));
    }

    /**
     * Adds a new animation sequence to this Animation Component with the animation step used as the setpoint
     * @param field The field who's value is updated
     *
     */
    public void addAnimationSequence(AnimationField field){
        this.animationSequenceList.add(new AnimationSequence(field, null));
    }

    private void updateAnimations(){
        for(AnimationSequence animationSequence : this.animationSequenceList){
            if(animationSequence.sequence == null){
                animationSequence.field.value = this.currentStep;
            } else {
                animationSequence.field.value = animationSequence.sequence[this.currentStep];
            }
        }
    }

    public void restart(){
        currentStep = 0;
        currentTime = 0;
    }

    @Override
    public void onTick(long nanosSincePreviousTick){
        this.currentTime -= nanosSincePreviousTick;
        justFinished = false; // only set to true when transitioning back to first frame.
        if(this.currentTime < 0) {
            while (this.currentTime < 0) {
                this.currentTime += this.frameDuration;
                this.currentStep = (this.currentStep + 1) % this.steps;
            }
            this.updateAnimations();
            if(!justFinished && this.currentStep == 0){
                this.justFinished = true;
            }
        }
    }

    @Override
    public void onLateTick(){}

    @Override
    public int getSystemFlags() {
        return SystemFlag.TickSystem;
    }

    @Override
    public String getTag() {
        return "TestAnimationComponent";
    }
}
