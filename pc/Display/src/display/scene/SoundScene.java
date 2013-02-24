/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package display.scene;

import ddf.minim.analysis.FFT;
import display.draw.Image;
import display.scene.sound.AudioSource;
import display.scene.sound.SoundSourceListener;


enum Style {
    
    BAR,
    COLOR;
    
}
/**
 *
 * @author wilson
 */
public class SoundScene extends Scene implements SoundSourceListener{

    final static double scale = 0.0005;
    final static double decay = 0.6;
    
    Style style = Style.BAR;
    
    int maxX; // Maximum x coordinate, limited by fft's available bands and display width
    @Override
    public void drawFrame(Image img, float delta) {
        
        for (int x=0;x<maxX;x++) {
            
            
            
            double val = fft.getAvg(x)*scale;
            
            if (style==Style.BAR) {
                double pixelH = 1f/Image.HEIGHT;
                for (int y=Image.HEIGHT-1;y>=0;y--) {
                    if (val>pixelH)
                        img.data[y][x]=1;
                    else if (val<=0) {
                        img.data[y][x]=0;
                    } else {
                        img.data[y][x]=(float) (val / pixelH);
                    }
                    val-=pixelH;
                }
            } else if (style==Style.COLOR) {
                //Clamp to (0,1)
                val = val>1?1:val<0?0:val;
                for (int y=0;y<Image.HEIGHT;y++) {
                    img.data[y][x]=(float) val;
                }
                
            }
        }
        
        
    }

    @Override
    public String getName() {
        return "Sound";
    }

    @Override
    public void dataArrived(byte[] data) {
        float[] realData = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            realData[i] = data[i];
        }

        fft.forward(realData);
    }
    
    FFT fft;
    public SoundScene(AudioSource source) {
        fft = new FFT(source.getBufferSize(), source.getSamplesPerSecond());
        source.addSoundSourceListener(this);
        fft.logAverages(40, 7);
        
        maxX=Image.WIDTH>fft.avgSize()?fft.avgSize():Image.WIDTH;
    }


    
}
