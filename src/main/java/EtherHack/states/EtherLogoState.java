package EtherHack.states;

import zombie.GameTime;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.gameStates.GameState;
import zombie.gameStates.GameStateMachine;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.ui.UIManager;

public class EtherLogoState
extends GameState {
    private float alpha = 0.0f;
    private float logoDisplayTime = 40.0f;
    private int stage = 0;
    private float targetAlpha = 0.0f;
    private boolean noRender = false;
    private final LogoElement etherLogo = new LogoElement("EtherHack/media/EtherLogo.png");

    public void enter() {
        UIManager.bSuspend = true;
        this.alpha = 0.0f;
        this.targetAlpha = 1.0f;
    }

    public void exit() {
        UIManager.bSuspend = false;
    }

    public void render() {
        Core core = Core.getInstance();
        if (this.noRender) {
            core.StartFrameUI();
            SpriteRenderer.instance.renderi(null, 0, 0, core.getOffscreenWidth(0), core.getOffscreenHeight(0), 0.0f, 0.0f, 0.0f, 1.0f, null);
            core.EndFrame();
        } else {
            core.StartFrameUI();
            core.EndFrame();
            boolean tempUseUIFBO = UIManager.useUIFBO;
            UIManager.useUIFBO = false;
            core.StartFrameUI();
            SpriteRenderer.instance.renderi(null, 0, 0, core.getOffscreenWidth(0), core.getOffscreenHeight(0), 0.0f, 0.0f, 0.0f, 1.0f, null);
            this.etherLogo.centerOnScreen();
            this.etherLogo.render(this.alpha);
            core.EndFrameUI();
            UIManager.useUIFBO = tempUseUIFBO;
        }
    }

    public GameStateMachine.StateAction update() {
        if (Mouse.isLeftDown() || GameKeyboard.isKeyDown((int)28) || GameKeyboard.isKeyDown((int)57) || GameKeyboard.isKeyDown((int)1)) {
            this.stage = 2;
        }
        GameTime gameTime = GameTime.getInstance();
        switch (this.stage) {
            case 0: {
                this.targetAlpha = 1.0f;
                if (this.alpha != 1.0f) break;
                this.stage = 1;
                break;
            }
            case 1: {
                this.logoDisplayTime -= gameTime.getMultiplier() / 1.6f;
                if (!(this.logoDisplayTime <= 0.0f)) break;
                this.stage = 2;
                break;
            }
            case 2: {
                this.targetAlpha = 0.0f;
                if (this.alpha != 0.0f) break;
                this.noRender = true;
                return GameStateMachine.StateAction.Continue;
            }
        }
        this.updateAlpha(gameTime);
        return GameStateMachine.StateAction.Remain;
    }

    private void updateAlpha(GameTime gameTime) {
        float alphaStep = 0.02f;
        float deltaTime = alphaStep * gameTime.getMultiplier();
        if (this.alpha < this.targetAlpha) {
            this.alpha += deltaTime;
            if (this.alpha > this.targetAlpha) {
                this.alpha = this.targetAlpha;
            }
        } else if (this.alpha > this.targetAlpha) {
            this.alpha -= deltaTime;
            if (this.stage == 2) {
                this.alpha -= deltaTime;
            }
            if (this.alpha < this.targetAlpha) {
                this.alpha = this.targetAlpha;
            }
        }
    }

    private static final class LogoElement {
        private final Texture texture;
        private int x;
        private int y;
        private int width;
        private int height;

        LogoElement(String texturePath) {
            this.texture = Texture.getSharedTexture((String)texturePath);
            if (this.texture != null) {
                this.width = this.texture.getWidth();
                this.height = this.texture.getHeight();
            }
        }

        void centerOnScreen() {
            Core core = Core.getInstance();
            this.x = (core.getScreenWidth() - this.width) / 2;
            this.y = (core.getScreenHeight() - this.height) / 2;
        }

        void render(float alpha) {
            if (this.texture != null && this.texture.isReady()) {
                SpriteRenderer.instance.renderi(this.texture, this.x, this.y, this.width, this.height, 1.0f, 1.0f, 1.0f, alpha, null);
            }
        }
    }
}
