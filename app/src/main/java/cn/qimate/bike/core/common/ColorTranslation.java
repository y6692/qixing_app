package cn.qimate.bike.core.common;

/**
 * Created by zzz40500 on 15/2/2.
 */
public class ColorTranslation {





    public static  int translation(int startColor,int endColor,float percentage){



        int startInt = startColor;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;
        int endInt = endColor;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;
        return ((startA + (int) (percentage * (endA - startA))) << 24)
                | ((startR + (int) (percentage * (endR - startR))) << 16)
                | ((startG + (int) (percentage * (endG - startG))) << 8)
                | (startB + (int) (percentage * (endB - startB)));
    }

}
