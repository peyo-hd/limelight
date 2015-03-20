package com.limelight.binding.video;

import java.nio.ByteBuffer;
import android.util.Log;

import com.limelight.LimeLog;
import com.limelight.nvstream.av.ByteBufferDescriptor;
import com.limelight.nvstream.av.DecodeUnit;
import com.limelight.nvstream.av.video.VideoDepacketizer;

public class AmCodecDecoderRenderer extends EnhancedDecoderRenderer {
	private String TAG = "AmCodecDecoderRenderer";
	
    private Thread decoderThread;

    private static final int DECODER_BUFFER_SIZE = 512*1024;
    private ByteBuffer decoderBuffer;
    private AmCodec amcodec;
    private int mWidth, mHeight;

    @Override
    public boolean setup(int width, int height, int redrawRate, Object renderTarget, int drFlags) {
        decoderBuffer = ByteBuffer.allocateDirect(DECODER_BUFFER_SIZE);
        amcodec = new AmCodec();
        mWidth = width;
        mHeight = height;
        LimeLog.info("AmCodecDecoderRenderer.setup() width:" + width + " height:" + height);
        return true;
    }

    @Override
    public boolean start(final VideoDepacketizer depacketizer) {
        decoderThread = new Thread() {
            @Override
            public void run() {
                amcodec.init(mWidth, mHeight);
                DecodeUnit du;
                while (!isInterrupted()) {
                    try {
                        du = depacketizer.takeNextDecodeUnit();
                    } catch (InterruptedException e) {
                        break;
                    }

                    submitDecodeUnit(du);
                    depacketizer.freeDecodeUnit(du);
                }
            	while (amcodec.buflen() > 0x100) {
           	 		try {
        				Thread.sleep(100);
        			} catch (InterruptedException e) {
        				e.printStackTrace();
        			}
           	 	}
               	amcodec.close();
            }
        };
        decoderThread.setName("Video - Decoder (AmCodec)");
        decoderThread.setPriority(Thread.MAX_PRIORITY - 1);
        decoderThread.start();
        return true;
    }


    @Override
    public void stop() {
        decoderThread.interrupt();
        try {
            decoderThread.join();
        } catch (InterruptedException e) { }

    }

    @Override
    public void release() {

    }

    private boolean submitDecodeUnit(DecodeUnit decodeUnit) {
        decoderBuffer.clear();
        for (ByteBufferDescriptor bbd = decodeUnit.getBufferHead();
                bbd != null; bbd = bbd.nextDescriptor) {
           decoderBuffer.put(bbd.data, bbd.offset, bbd.length);
        }

    	int size = decodeUnit.getDataLength();
        if (size == 0) {
        	Log.i(TAG, "decodeUnit.getDataLength() is 0");
        	return false;
         } else {
    		 int offset = 0;
    		 int ret = 0;
        	 do {
        		ret = amcodec.write(decoderBuffer, offset, size);
        		if (ret < 0) {
           		 Log.i(TAG, "amcodec.write(" + size + ") returned " + ret);
           		 return false;	
        		} else {
        			size -= ret;
        			offset += ret;
        		}
        	 } while ((ret >= 0) && (size > 0));
             return true;
        }
    }

    @Override
    public int getCapabilities() {
        return 0;
    }

    @Override
    public int getAverageDecoderLatency() {
        return 0;
    }

    @Override
    public int getAverageEndToEndLatency() {
        return 0;
    }

    @Override
    public String getDecoderName() {
        return "AmCodec decoding";
    }
}
