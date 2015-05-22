package com.greycodes.zerito;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.greycodes.zerito.app.AppController;



public class PreviewActivity extends ActionBarActivity {
ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        getSupportActionBar().hide();

        imageView = (ImageView) findViewById(R.id.preview_image);
        Resources res = getApplicationContext().getResources();
        String text=getIntent().getStringExtra("msg");//max of 52 characters
        Bitmap background;
       // background = BitmapFactory.decodeResource(res, R.drawable.back)
        background= AppController.imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(background);
        Typeface font = null;
        try {
            font = Typeface.createFromAsset(res.getAssets(), "fonts/asd.ttf");
            font = Typeface.create(font, Typeface.BOLD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Paint paint = new Paint();
        try {
            paint.setTypeface(font);
        } catch (Exception e) {
            e.printStackTrace();
        }
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setShadowLayer(2.0f, 1.0f, 1.0f, Color.BLACK);
        float fontSize = 50;
        //getFontSize(background.getWidth(), "HELLO TESTING", paint); //You'll have to define a way to find a size that fits, or just use a constant size.

        paint.setTextSize(fontSize);
        canvas.drawText(text, (background.getWidth() - paint.measureText(text)) / 2,
                background.getHeight()/2, paint); //You might want to do something different. In my case every image has a filler in the bottom which is 50px.


        imageView.setImageBitmap(background);
     /*   try {
            WallpaperManager myWallpaperManager = WallpaperManager
                    .getInstance(getApplicationContext());
            myWallpaperManager.setBitmap(background);
            Toast.makeText(getApplicationContext(), "Wallpaper set",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    e +" Error setting Wallpaper", Toast.LENGTH_LONG)
                    .show();
        } */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
