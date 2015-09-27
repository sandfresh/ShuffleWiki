package gikolab.com.shufflewiki.value;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by stalin on 2015/9/28.
 */
public class ImageGet
{

    String mUrl;

    public ImageGet(String url)
    {
        mUrl = url;
    }

    public Bitmap get()
    {
        try
        {
            URL url = new URL(mUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;

            byte[] data = inputStream2ByteArr(input);
            BitmapFactory.decodeByteArray(data, 0, data.length, opts);

            opts.inSampleSize = calculateInSampleSize(opts, 800, 480);
            opts.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);

            return bitmap;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

    }

    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private byte[] inputStream2ByteArr(InputStream inputStream) throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buff)) != -1) {
            outputStream.write(buff, 0, len);
        }
        inputStream.close();
        outputStream.close();
        return outputStream.toByteArray();
    }
}
