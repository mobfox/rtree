package com.mobfox.rtree.model;

import com.mobfox.rtree.benchmark.Benchmarker;
import com.mobfox.rtree.entity.TrainingSample;
import com.mobfox.rtree.function.impl.MeanLeafFitter;
import com.mobfox.rtree.function.impl.VarianceReductionNodeSplitter;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class RTreeTestIT {
    @Test
    public void simpleTest() throws IOException, URISyntaxException {
        // the features of our training dataset
        final List<String> features = features();

        // load our training dataset
        final Path testFile = Paths.get(getClass().getClassLoader().getResource("test_data.tsv").toURI());
        final List<TrainingSample> samples = Files.lines(testFile)
                .filter(line -> !"".equals(line))
                .map(line -> lineToSample(features, line))
                .collect(Collectors.toList());

        // train a model
        final RTree model = RTree.build(
                new VarianceReductionNodeSplitter(50, 0.5, 0.1),
                new MeanLeafFitter(),
                new HashSet<>(features), samples);
        // print some stats about our model
        System.out.println(model.stats());

        // evaluate our model in an in-sample benchmark
        final Benchmarker.Result result = Benchmarker.benchmark(model, samples);
        System.out.println(result);
    }

    private List<String> features() {
        final List<String> features = new ArrayList<>();
        features.add("country_code");
        features.add("city_code");
        features.add("state_code");
        features.add("inventory_hash");
        features.add("device_os");
        features.add("device_os_version");
        features.add("request_type");
        features.add("geo_lon");
        features.add("adspace_width");
        features.add("adspace_height");
        features.add("udid_ifa");
        return features;
    }

    private TrainingSample lineToSample(final List<String> features, final String line) {
        final String[] tokens = line.split("\t");
        final Map<String, String> featureValues = new HashMap<>();
        for (int i = 0; i < features.size(); ++i) {
            featureValues.put(features.get(i), tokens[i]);
        }
        final String valueToken = tokens[tokens.length - 1];
        return new TrainingSample(featureValues, valueToken.equals("null") ? 0.0 : Double.valueOf(valueToken));
    }
}