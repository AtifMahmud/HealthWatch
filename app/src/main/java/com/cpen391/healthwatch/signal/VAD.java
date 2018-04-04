package com.cpen391.healthwatch.signal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by william on 2018-04-01.
 * This class provides methods to detect voice in 8-bit audio samples.
 * The main algorithm is based on the following paper.
 * References: http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.176.6740&rep=rep1&type=pdf
 */

public class VAD {
    private static final String TAG = VAD.class.getSimpleName();
    private final int FRAME_SIZE = 10; // 10ms frames

    // threshold parameters set based on paper in reference
    private final int ENERGY_PRIM_THRESH = 40;
    private final int F_PRIM_THRESH = 185;
    private final int SF_PRIM_THRESH = 5;

    private int mNumFrames;
    private int mFrameSamples;
    private int mSamplingRate;

    private List<List<Byte>> mFrames;

    private double[] mEnergy;
    private double[] mFreq;
    private double[] mSFM;


    private FFT mFFT;

    /**
     * @param windowSize   size of audio window in milliseconds.
     * @param samplingRate the sampling rate for the audio samples in Hz.
     */
    public VAD(int windowSize, int samplingRate) {
        mNumFrames = windowSize / FRAME_SIZE;
        mSamplingRate = samplingRate;
        mEnergy = new double[mNumFrames];
        mFreq = new double[mNumFrames];
        mSFM = new double[mNumFrames];
        // Number of samples in one frame.
        mFrameSamples = (int) Math.round(samplingRate * FRAME_SIZE * Math.pow(10, -3));
        initFFT();
    }

    private void initFFT() {
        int i = 2;
        while (i < mFrameSamples) {
            i *= 2;
        }
        mFFT = new FFT(i / 2);
    }

    /**
     * @param samples the audio samples, with 8-bit resolution sampled at the sampling rate
     *                passed in when object was created. The number of samples must be the
     *                number of samples in 1 windowSize.
     */
    public boolean vad(byte[] samples) {
        boolean minSet = false;
        double minE = 0, minF = 0, minSF = 0;
        double threshE, threshF, threshSF;
        threshE = ENERGY_PRIM_THRESH;
        threshF = F_PRIM_THRESH;
        threshSF = SF_PRIM_THRESH;
        // Indicates whether ith frame is silence, true, or has voice, false.
        boolean[] frameSilence;
        // The number of silence frames.
        int numSilence = 0;
        int counter;
        splitToFrames(samples);
        frameSilence = new boolean[mNumFrames];
        for (int i = 0; i < mNumFrames; i++) {
            mEnergy[i] = computeFrameEnergy(i);
            mFreq[i] = computeMaxFreqComponent(i);
            if (!minSet) {
                minE = mEnergy[i];
                minF = mFreq[i];
                minSF = mSFM[i];
                minSet = true;
            } else {
                if (mEnergy[i] < minE) {
                    minE = mEnergy[i];
                    threshE = ENERGY_PRIM_THRESH * Math.log(minE);
                }
                if (mFreq[i] < minF) {
                    minF = mFreq[i];
                }
                if (mSFM[i] < minSF) {
                    minSF = mSFM[i];
                }
            }
            //Log.d(TAG, "Min energy: " + minE);
            //Log.d(TAG, "Min freq: " + minF);
            //Log.d(TAG, "Min spectral flatness: " + minSF);
            counter = 0;
            if (mEnergy[i] - minE >= threshE) {
                counter++;
            }
            if (mFreq[i] - minF >= threshF) {
                counter++;
            }
            if (mSFM[i] - minSF >= threshSF) {
                counter++;
            }
            //Log.d(TAG, "counter: " + counter);
            frameSilence[i] = counter <= 1;
            if (frameSilence[i]) {
                numSilence++;
                minE = ((numSilence * minE) + mEnergy[i]) / (numSilence + 1);
                threshE = threshE * Math.log(minE);
            }
        }
        return isSpeech(frameSilence);
    }

    // Check if speech is present.
    private boolean isSpeech(boolean[] frameSilence) {
        final int SPEECH_COUNT = 4;
        int speechCount = 0;
        for (boolean isSilence : frameSilence) {
            if (speechCount > SPEECH_COUNT) {
                break;
            }
            speechCount = isSilence ? 0 : speechCount + 1;
        }
        return speechCount > SPEECH_COUNT;
    }

    /**
     * Compute the spectral flatness measure.
     *
     * @return the spectral flatness measure of the kth frame.
     */
    private double computeSpectralFlatness(double[] p) {
        double Gm = 0;
        double Am = 0;
        for (double val : p) {
            Gm += Math.log(val);
            Am += val;
        }
        Gm /= p.length;
        Am /= p.length;
        Gm = Math.exp(Gm);
        return 10 * Math.log10(Gm / Am);
    }

    /**
     * Compute maximum frequency component of a frame. Also the spectral flatness as well.
     *
     * @param k the kth frame.
     * @return the max frequency component of the kth frame.
     */
    private double computeMaxFreqComponent(int k) {
        List<Byte> frame = padFrame(k);
        double[] x = new double[frame.size()];
        double[] y = new double[frame.size()];
        for (int i = 0; i < frame.size(); i++) {
            x[i] = frame.get(i);
            y[i] = 0;
        }
        mFFT.fft(x, y);
        // FFT applied over real numbers result in symmetrical results so we only
        // need to have half the size for p.
        double[] p = new double[frame.size() / 2];
        for (int i = 0; i < p.length; i++) {
            p[i] = x[i + p.length] * x[i + p.length] + y[i + p.length] * y[i + p.length];
        }
        double maxFreq = (mSamplingRate / 2) + findMax(p) * (mSamplingRate / frame.size());
        //Log.d(TAG, "MaxFreq: " + maxFreq);
        mSFM[k] = computeSpectralFlatness(p);
        return maxFreq;
    }

    /**
     * @param a the array to find max in, a.length > 0.
     * @return the index of the max value in array a.
     */
    private double findMax(double[] a) {
        double max = a[0];
        int maxIndex = 0;
        for (int i = 1; i < a.length; i++) {
            if (a[i] > max) {
                max = a[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    /**
     * Pads the kth frame with 0s to achieve number of samples that is a power of 2 to allow us to use
     * FFT.
     *
     * @param k the kth frame to pad.
     * @return a newly created frame that is padded with zeros.
     */
    private List<Byte> padFrame(int k) {
        List<Byte> paddedFrame = new ArrayList<>();
        paddedFrame.addAll(mFrames.get(k));
        int frameSize = paddedFrame.size();
        int i = 2;
        while (i < frameSize) {
            i *= 2;
        }
        int count = 0;
        int numberOfZeros = i - frameSize;
        while (count < numberOfZeros) {
            paddedFrame.add((byte) 0);
            count++;
        }
        return paddedFrame;
    }

    /**
     * Computes the energy of a frame, require that the frames are properly initialized.
     *
     * @param k the kth frame to compute energy for.
     * @return the energy of the kth frame.
     */
    private double computeFrameEnergy(int k) {
        double energy = 0;
        for (Byte b : mFrames.get(k)) {
            energy += b * b;
        }
        //Log.d(TAG, "energy: " + energy);
        return energy;
    }

    /**
     * @param samples the audio samples.
     */
    private void splitToFrames(byte[] samples) {
        mFrames = new ArrayList<>();
        for (int i = 0; i < mNumFrames; i++) {
            List<Byte> frame = new ArrayList<>();
            try {
                for (int j = i * mFrameSamples; j < (i + 1) * mFrameSamples; j++) {
                    frame.add(samples[j]);
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                i = mNumFrames; // Set so we leave function afterwards.
            }
            mFrames.add(frame);
        }
    }
}
