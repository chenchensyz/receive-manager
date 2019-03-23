
package cn.com.cyber.util;

import cn.com.cyber.model.CodeInfo;

import java.util.List;
import java.util.Map;

public class CodeInfoUtils {

    public static Map<String, CodeInfo> codeInfoMap;

    public static Map<String, List<CodeInfo>> codeListMap;

    public static void setCodeInfoMap(Map<String, CodeInfo> map) {
        codeInfoMap = map;
    }

    public static Map<String, CodeInfo> getCodeByNameAndType() {
        return codeInfoMap;
    }

    public static void setCodeListMap(Map<String, List<CodeInfo>> map) {
        codeListMap = map;
    }

    public static Map<String, List<CodeInfo>> getCodeListByType() {
        return codeListMap;
    }


}
