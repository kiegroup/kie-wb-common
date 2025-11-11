package org.guvnor.ala.util;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.text.StrLookup;

import java.beans.PropertyDescriptor;
import java.util.Map;

public final class VariableLookup extends StrLookup {

    private final Map<String, Object> lookupMap;

    public VariableLookup(final Map<String, Object> lookupMap) {
        this.lookupMap = lookupMap;
    }

    @Override
    public String lookup(String key) {
        if (this.lookupMap == null) {
            return null;
        } else {
            int dotIndex = key.indexOf(".");
            Object obj = this.lookupMap.get(key.substring(0, dotIndex < 0 ? key.length() : dotIndex));
            if (obj instanceof Map) {
                return new VariableLookup(((Map) obj)).lookup(key.substring(key.indexOf(".") + 1));
            } else if (obj != null && !(obj instanceof String) && key.contains(".")) {
                final String subkey = key.substring(key.indexOf(".") + 1);
                for (PropertyDescriptor descriptor : new PropertyUtilsBean().getPropertyDescriptors(obj)) {
                    if (descriptor.getName().equals(subkey)) {
                        try {
                            return descriptor.getReadMethod().invoke(obj).toString();
                        } catch (Exception ex) {
                            // It was probably meant to just continue looping, so I leave an empty catch here.
                        }
                    }
                }
            }

            return obj == null ? "" : obj.toString();
        }
    }
}
