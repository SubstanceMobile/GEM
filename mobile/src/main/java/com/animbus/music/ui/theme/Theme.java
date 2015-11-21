package com.animbus.music.ui.theme;

/**
 * Created by Adrian on 8/5/2015.
 */
public class Theme {
    int base;
    int colorPrimary;
    int colorAccent;
    int colorGrey;
    int colorComplimentary;
    int colorComplimentaryGrey;

    int statusBarColor;
    int backgroundColor;


    protected Theme(int base, int primary, int accent, int grey, int complimentary, int complimentaryGrey) {
        this.base = base;
        this.colorPrimary = primary;
        this.colorAccent = accent;
        this.colorGrey = grey;
        this.colorComplimentary = complimentary;
        this.colorComplimentaryGrey = complimentaryGrey;
    }

    public int getBase() {
        return base;
    }

    public int getColorPrimary() {
        return colorPrimary;
    }

    public int getColorAccent() {
        return colorAccent;
    }

    public int getColorGrey() {
        return colorGrey;
    }

    public int getColorComplimentary() {
        return colorComplimentary;
    }

    public int getColorComplimentaryGrey() {
        return colorComplimentaryGrey;
    }

    static class Builder {
        private int base;
        private int colorPrimary;
        private int colorAccent;
        private int colorGrey;
        private int colorComplimentary;
        private int colorComplimentaryGrey;

        public Builder setBase(int base) {
            this.base = base;
            return this;
        }

        public Builder setColorPrimary(int colorPrimary) {
            this.colorPrimary = colorPrimary;
            return this;
        }

        public Builder setColorAccent(int colorAccent) {
            this.colorAccent = colorAccent;
            return this;
        }

        public Builder setColorGrey(int colorGrey) {
            this.colorGrey = colorGrey;
            return this;
        }

        public Builder setColorComplimentary(int colorComplimentary) {
            this.colorComplimentary = colorComplimentary;
            return this;
        }

        public Builder setColorComplimentaryGrey(int colorComplimentaryGrey) {
            this.colorComplimentaryGrey = colorComplimentaryGrey;
            return this;
        }

        public Theme build() {
            return new Theme(base,
                    colorPrimary,
                    colorAccent,
                    colorGrey,
                    colorComplimentary,
                    colorComplimentaryGrey);
        }

    }
}
