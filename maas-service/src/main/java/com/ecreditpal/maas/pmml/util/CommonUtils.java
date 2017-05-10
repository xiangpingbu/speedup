/**
 * Copyright [2012-2014] PayPal Software Foundation
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ecreditpal.maas.pmml.util;


import com.ecreditpal.maas.common.utils.json.JsonUtil;
import com.ecreditpal.maas.pmml.container.obj.ColumnConfig;
import com.ecreditpal.maas.pmml.container.obj.ModelConfig;
import com.ecreditpal.maas.pmml.container.obj.ModelTrainConf.ALGORITHM;
import com.ecreditpal.maas.pmml.container.obj.RawSourceData;
import com.ecreditpal.maas.pmml.core.LR;
import com.ecreditpal.maas.pmml.exception.ShifuErrorCode;
import com.ecreditpal.maas.pmml.exception.ShifuException;
import com.google.common.base.Splitter;
import com.google.common.collect.Collections2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.encog.ml.BasicML;
import org.encog.persist.EncogDirectoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;

/**
 * {@link CommonUtils} is used to for almost all kinds of utility function in this framework.
 */
public final class CommonUtils {

    /**
     * Avoid using new for our utility class.
     */
    private CommonUtils() {
    }

    private static final Logger log = LoggerFactory.getLogger(CommonUtils.class);


    /**
     * Load ModelConfig from local json ModelConfig.json file.
     */
    public static ModelConfig loadModelConfig(String path) throws IOException {
        return loadModelConfig(path, RawSourceData.SourceType.LOCAL);
    }

    /**
     * Load model configuration from the path and the source type.
     *
     * @throws IOException              if any IO exception in parsing json.
     * @throws IllegalArgumentException if {@code path} is null or empty, if sourceType is null.
     */
    public static ModelConfig loadModelConfig(String path, RawSourceData.SourceType sourceType) throws IOException {
        return loadJSON(path, sourceType, ModelConfig.class);
    }

    /**
     * Load column configuration list.
     *
     * @throws IOException if any IO exception in parsing json.
     */
    public static List<ColumnConfig> loadColumnConfigList() throws IOException {
        return loadColumnConfigList(Constants.LOCAL_COLUMN_CONFIG_JSON, RawSourceData.SourceType.HDFS);
    }

    public static List<ColumnConfig> loadColumnConfigList(String jsonConfig) {
        ColumnConfig[] configs = JsonUtil.fromJson(jsonConfig, ColumnConfig[].class);
        return  configs != null?Arrays.asList(configs):null;
    }

    /**
     * Load column configuration list.
     *
     * @throws IOException              if any IO exception in parsing json.
     * @throws IllegalArgumentException if {@code path} is null or empty, if sourceType is null.
     */
    public static List<ColumnConfig> loadColumnConfigList(String path, RawSourceData.SourceType sourceType) throws IOException {
        return Arrays.asList(loadJSON(path, sourceType, ColumnConfig[].class));
    }

    /**
     * Load JSON instance
     *
     * @throws IOException              if any IO exception in parsing json.
     * @throws IllegalArgumentException if {@code path} is null or empty, if sourceType is null.
     */
    public static <T> T loadJSON(String path, RawSourceData.SourceType sourceType, Class<T> clazz) throws IOException {
        checkPathAndMode(path, sourceType);
        log.debug("loading {} with sourceType {}", path, sourceType);
        BufferedReader reader = null;
        try {
            reader = getReader(path, sourceType);
            return JSONUtils.readValue(reader, clazz);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * Get buffered reader with <code>{@link Constants#DEFAULT_CHARSET}</code> for specified file
     * <p/>
     * !!! Warning: reader instance should be closed by caller.
     *
     * @param path       - file path
     * @param sourceType - local/hdfs
     * @return buffered reader with <code>{@link Constants#DEFAULT_CHARSET}</code>
     * @throws IOException - if any I/O exception in processing
     */
    public static BufferedReader getReader(String path, RawSourceData.SourceType sourceType) throws IOException {
        try {
            return new BufferedReader(new InputStreamReader(new FileInputStream(path)));
        } catch (IOException e) {
            throw e;
        }
    }


    private static void checkPathAndMode(String path, RawSourceData.SourceType sourceType) {
        if (StringUtils.isEmpty(path) || sourceType == null) {
            throw new IllegalArgumentException(String.format(
                    "path should not be null or empty, sourceType should not be null, path:%s, sourceType:%s", path,
                    sourceType));
        }
    }


    /**
     * Return header column list from header file.
     *
     * @throws IOException              if any IO exception in reading file.
     * @throws IllegalArgumentException if sourceType is null, if pathHeader is null or empty, if delimiter is null or empty.
     * @throws RuntimeException         if first line of pathHeader is null or empty.
     */
    public static String[] getHeaders(String pathHeader, String delimiter, RawSourceData.SourceType sourceType) throws IOException {
        return getHeaders(pathHeader, delimiter, sourceType, false);
    }

    /**
     * Return header column array from header file.
     *
     * @throws IOException              if any IO exception in reading file.
     * @throws IllegalArgumentException if sourceType is null, if pathHeader is null or empty, if delimiter is null or empty.
     * @throws RuntimeException         if first line of pathHeader is null or empty.
     */
    public static String[] getHeaders(String pathHeader, String delimiter, RawSourceData.SourceType sourceType, boolean isFull)
            throws IOException {
        if (StringUtils.isEmpty(pathHeader) || StringUtils.isEmpty(delimiter) || sourceType == null) {
            throw new IllegalArgumentException(String.format(
                    "Null or empty parameters srcDataPath:%s, dstDataPath:%s, sourceType:%s", pathHeader, delimiter,
                    sourceType));
        }
        BufferedReader reader = null;
        String pigHeaderStr = null;

        try {
            reader = getReader(pathHeader, sourceType);
            pigHeaderStr = reader.readLine();
            if (StringUtils.isEmpty(pigHeaderStr)) {
                throw new RuntimeException(String.format("Cannot reade header info from the first line of file: %s",
                        pathHeader));
            }
        } catch (Exception e) {
            log.error(
                    "Error in getReader, this must be catched in this method to make sure the next reader can be returned.",
                    e);
            throw new ShifuException(ShifuErrorCode.ERROR_HEADER_NOT_FOUND);
        } finally {
            IOUtils.closeQuietly(reader);
        }

        List<String> headerList = new ArrayList<String>();
        Set<String> headerSet = new HashSet<String>();
        int index = 0;
        for (String str : Splitter.on(delimiter).split(pigHeaderStr)) {
            String columnName;
            if (isFull) {
                columnName = getFullPigHeaderColumnName(str);
            } else {
                columnName = getRelativePigHeaderColumnName(str);
            }

            if (headerSet.contains(columnName)) {
                columnName = columnName + "_" + index;
            }
            headerSet.add(columnName);
            index++;
            headerList.add(columnName);
        }
        return headerList.toArray(new String[0]);
    }

    /**
     * Get full column name from pig header. For example, one column is a::b, return a_b. If b, return b.
     */
    public static String getFullPigHeaderColumnName(String raw) {
        return raw == null ? raw : raw.replaceAll(Constants.PIG_COLUMN_SEPARATOR, Constants.PIG_FULL_COLUMN_SEPARATOR);
        // return raw;
    }

    /**
     * Get relative column name from pig header. For example, one column is a::b, return b. If b, return b.
     *
     * @throws NullPointerException if parameter raw is null.
     */
    public static String getRelativePigHeaderColumnName(String raw) {
        int position = raw.lastIndexOf(Constants.PIG_COLUMN_SEPARATOR);
        return position >= 0 ? raw.substring(position + Constants.PIG_COLUMN_SEPARATOR.length()) : raw;
    }

    /**
     * Given a column value, return bin list index. Return 0 for Category because of index 0 is started from
     * NEGATIVE_INFINITY.
     *
     * @throws IllegalArgumentException if input is null or empty.
     * @throws NumberFormatException    if columnVal does not contain a parsable number.
     */
    public static int getBinNum(ColumnConfig columnConfig, String columnVal) {
        if (columnConfig.isCategorical()) {
            List<String> binCategories = columnConfig.getBinCategory();
            for (int i = 0; i < binCategories.size(); i++) {
                if (binCategories.get(i).equals(columnVal)) {
                    return i;
                }
            }
            return -1;
        } else {
            if (StringUtils.isBlank(columnVal)) {
                return -1;
            }
            double dval = 0.0;
            try {
                dval = Double.parseDouble(columnVal);
            } catch (Exception e) {
                return -1;
            }
            return getBinIndex(columnConfig.getBinBoundary(), dval);
        }
    }

    /**
     * Return the real bin number for one value. As the first bin value is NEGATIVE_INFINITY, invalid index is 0, not
     * -1.
     *
     * @param binBoundary bin boundary list which should be sorted.
     * @throws IllegalArgumentException if binBoundary is null or empty.
     */
    @SuppressWarnings("unused")
    private static int getNumericBinNum(List<Double> binBoundary, double value) {
        if (CollectionUtils.isEmpty(binBoundary)) {
            throw new IllegalArgumentException("binBoundary should not be null or empty.");
        }

        int n = binBoundary.size() - 1;
        while (n > 0 && value < binBoundary.get(n)) {
            n--;
        }
        return n;
    }

    /**
     * Common split function to ignore special character like '|'. It's better to return a list while many calls in our
     * framework using string[].
     *
     * @throws IllegalArgumentException {@code raw} and {@code delimiter} is null or empty.
     */
    public static String[] split(String raw, String delimiter) {
        return splitAndReturnList(raw, delimiter).toArray(new String[0]);
    }

    /**
     * Common split function to ignore special character like '|'.
     *
     * @throws IllegalArgumentException {@code raw} and {@code delimiter} is null or empty.
     */
    public static List<String> splitAndReturnList(String raw, String delimiter) {
        if (StringUtils.isEmpty(raw) || StringUtils.isEmpty(delimiter)) {
            throw new IllegalArgumentException(String.format(
                    "raw and delimeter should not be null or empty, raw:%s, delimeter:%s", raw, delimiter));
        }
        List<String> headerList = new ArrayList<String>();
        for (String str : Splitter.on(delimiter).split(raw)) {
            headerList.add(str);
        }
        return headerList;
    }

    /**
     * Get target column.
     *
     * @throws IllegalArgumentException if columnConfigList is null or empty.
     * @throws IllegalStateException    if no target column can be found.
     */
    public static Integer getTargetColumnNum(List<ColumnConfig> columnConfigList) {
        if (CollectionUtils.isEmpty(columnConfigList)) {
            throw new IllegalArgumentException("columnConfigList should not be null or empty.");
        }
        // I need cast operation because of common-collections dosen't support generic.
        ColumnConfig cc = (ColumnConfig) CollectionUtils.find(columnConfigList, new Predicate() {
            public boolean evaluate(Object object) {
                return ((ColumnConfig) object).isTarget();
            }
        });
        if (cc == null) {
            throw new IllegalStateException("No target column can be found, please check your column configurations");
        }
        return cc.getColumnNum();
    }


    public static List<BasicML> loadSpecificBasicModels(final String params, final ALGORITHM alg) throws IOException {
        if (params == null || alg == null || ALGORITHM.DT.equals(alg)) {
            throw new IllegalArgumentException("The model path shouldn't be null");
        }
        List<BasicML> models = new ArrayList<BasicML>(1);

        if (ALGORITHM.NN.equals(alg)) {
            InputStream is = new ByteArrayInputStream(params.getBytes());
            models.add(BasicML.class.cast(EncogDirectoryPersistence.loadObject(is)));
        } else if (ALGORITHM.LR.equals(alg)) {
            models.add(LR.loadFromString(params));
        }
        return models;
    }

/**
 * Load neural network models from specified file path
 *
 * @param modelsPath
 * - a file or directory that contains .nn files
 * @return - a list of @BasicML
 * @throws IOException
 * - throw exception when loading model files
 */
public static List<BasicML> loadBasicModels(final String modelsPath,final ALGORITHM alg)throws IOException{
        if(modelsPath==null||alg==null||ALGORITHM.DT.equals(alg)){
        throw new IllegalArgumentException("The model path shouldn't be null");
        }

        // we have to register PersistBasicFloatNetwork for loading such models

        /*if(ALGORITHM.NN.equals(alg)) {
            PersistorRegistry.getInstance().add(new PersistBasicFloatNetwork());
        }*/

        File modelsPathDir=new File(modelsPath);

        File[]modelFiles=modelsPathDir.listFiles(new FilenameFilter(){

public boolean accept(File dir,String name){
        return name.endsWith("."+alg.name().toLowerCase());
        }
        });

        if(modelFiles!=null){
        // sort file names
        Arrays.sort(modelFiles,new Comparator<File>(){
public int compare(File from,File to){
        return from.getName().compareTo(to.getName());
        }
        });

        List<BasicML> models=new ArrayList<BasicML>(modelFiles.length);
        for(File nnf:modelFiles){
        InputStream is=null;
        try{
        is=new FileInputStream(nnf);
        if(ALGORITHM.NN.equals(alg)){
        models.add(BasicML.class.cast(EncogDirectoryPersistence.loadObject(is)));
        }else if(ALGORITHM.LR.equals(alg)){
        models.add(LR.loadFromStream(is));
        }
        }finally{
        IOUtils.closeQuietly(is);
        }
        }

        return models;
        }else{
        throw new IOException(String.format("Failed to list files in %s",modelsPathDir.getAbsolutePath()));
        }
        }


public static boolean isDesicionTreeAlgorithm(String alg){
        return"RF".equalsIgnoreCase(alg)||"GBT".equalsIgnoreCase(alg);
        }

/**
 * Get bin index by binary search. The last bin in <code>binBoundary</code> is missing value bin.
 */
public static int getBinIndex(List<Double> binBoundary,Double dVal){
        assert binBoundary!=null&&binBoundary.size()>0;
        assert dVal!=null;
        int binSize=binBoundary.size();

        int low=0;
        int high=binSize-1;

        while(low<=high){
        int mid=(low+high)>>>1;
        Double midVal=binBoundary.get(mid);
        int cmp=midVal.compareTo(dVal);

        if(cmp< 0){
        low=mid+1;
        }else if(cmp>0){
        high=mid-1;
        }else{
        return mid; // key found
        }
        }

        return low==0?0:low-1;
        }


/**
 * Return one HashMap Object contains keys in the first parameter, values in the second parameter. Before calling
 * this method, you should be aware that headers should be unique.
 *
 * @throws IllegalArgumentException
 *             if lengths of two arrays are not the same.
 * @throws NullPointerException
 *             if header or data is null.
 */
public static Map<String, String> getRawDataMap(String[]header,String[]data){
        if(header.length!=data.length){
        throw new IllegalArgumentException(String.format("Header/Data mismatch: Header length %s, Data length %s",
        header.length,data.length));
        }

        Map<String, String> rawDataMap=new HashMap<String, String>(header.length);
        for(int i=0;i<header.length;i++){
        rawDataMap.put(header[i],data[i]);
        }
        return rawDataMap;
        }


/**
 * Return map entries sorted by value.
 */
public static<K, V extends Comparable<V>> List<Entry<K, V>>getEntriesSortedByValues(Map<K, V> map){
        List<Entry<K, V>>entries=new LinkedList<Entry<K, V>>(map.entrySet());

        Collections.sort(entries,new Comparator<Entry<K, V>>(){
public int compare(Entry<K, V> o1,Entry<K, V> o2){
        return o1.getValue().compareTo(o2.getValue());
        }
        });

        return entries;
        }


        }