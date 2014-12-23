package jp.crudefox.mymon.picturetool.api.input.common;

/**
 * Created by chikara on 2014/09/25.
 */
public final class Rect {


    public int left;
    public int top;
    public int right;
    public int bottom;


    public static final Rect valueOf(String str){
        String[] split = str.split(",",4);
        if( split.length != 4 ) return null;
        int[] arr = new int[]{
                Integer.valueOf( split[0] ),
                Integer.valueOf( split[1] ),
                Integer.valueOf( split[2] ),
                Integer.valueOf( split[3] )
        };
        return valueOf(arr);
    }
    public static final Rect valueOf(int[] arr){
        Rect rc = new Rect();
        rc.left = arr[0];
        rc.top = arr[1];
        rc.right = arr[2];
        rc.bottom = arr[3];
        return rc;
    }

    @Override
    public String toString() {
        return Rect.toString( this );
    }



    public static final String toString(Rect rc){
        int[] arr = toIntArray(rc);
        return String.format(
                "%d,%d,%d,%d",
                String.valueOf(arr[0]),
                String.valueOf(arr[1]),
                String.valueOf(arr[2]),
                String.valueOf(arr[3])
        );
    }
    public static final int[] toIntArray(Rect rc){
        return new int[]{
                rc.left,
                rc.top,
                rc.right,
                rc.bottom
        };
    }
}
