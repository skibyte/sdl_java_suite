package com.smartdevicelink.streaming.video;

import com.smartdevicelink.proxy.rpc.ImageResolution;
import com.smartdevicelink.proxy.rpc.VideoStreamingCapability;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class VideoStreamingParametersTest {
    private VideoStreamingParameters params;
    private VideoStreamingParameters otherParameters;
    private VideoStreamingCapability capability;
    private ImageResolution preferredResolution;

    @Before
    public void setUp() {
        params = new VideoStreamingParameters();
        capability = new VideoStreamingCapability();
        otherParameters = new VideoStreamingParameters();
    }

    @Test
    public void defaultValue_Scale() {
        assertEquals(1.0, params.getScale(), 0.005);
    }

    @Test
    public void update_Scale() {
        double expectedValue = 3.0;
        otherParameters.setScale(expectedValue);
        params = new VideoStreamingParameters(otherParameters);
        assertEquals(expectedValue, params.getScale(), 0.005);
    }

    @Test
    public void test_toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("VideoStreamingParams - format: {codec=H264, protocol=RAW}, ");
        builder.append("resolution: {576, 1024}, ");
        builder.append("frame rate: {30}, ");
        builder.append("displayDensity: {240}, ");
        builder.append("bitrate: {512000}, ");
        builder.append("IFrame interval: {5}, ");
        builder.append("scale: {1.0}");

        assertEquals(builder.toString(), params.toString());
    }

    @Test
    public void update_Scale_1_Resolution_800_354() {
        preferredResolution = new ImageResolution(800, 354);

        capability.setScale(1.0);
        capability.setPreferredResolution(preferredResolution);

        params.update(capability);

        int width = params.getResolution().getResolutionWidth();
        int height = params.getResolution().getResolutionHeight();

        assertEquals(800, width);
        assertEquals(354, height);
    }

    @Test
    public void update_Scale_1_25_Resolution_1280_569() {
        preferredResolution = new ImageResolution(1280, 569);

        capability.setScale(1.25);
        capability.setPreferredResolution(preferredResolution);

        params.update(capability);

        int width = params.getResolution().getResolutionWidth();
        int height = params.getResolution().getResolutionHeight();

        assertEquals(1024, width);
        assertEquals(455, height);
    }

    @Test
    public void update_Scale_1_5_Resolution_1280_569() {
        preferredResolution = new ImageResolution(1280, 569);

        capability.setScale(1.5);
        capability.setPreferredResolution(preferredResolution);

        params.update(capability);

        int width = params.getResolution().getResolutionWidth();
        int height = params.getResolution().getResolutionHeight();

        assertEquals(853, width);
        assertEquals(379, height);
    }
}