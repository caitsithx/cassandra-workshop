/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.builder.CompareToBuilder;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
 *
 */
public class EncodedVO implements Serializable{
    private static Comparator<byte[]> BYTE_ARRAY_COMPARATOR = new Comparator<byte[]>() {
            @Override
            public int compare(byte[] o1, byte[] o2) {
                return new CompareToBuilder().append(o1, o2).toComparison();
            }
        };
    
    private byte[] m_key = null;
    private Map<byte[], byte[]> m_values = new HashMap<>();
    private ArrayList<byte[]> m_keyArrList = new ArrayList<>();
    
    
    public byte[] getKey() {
        return m_key;
    }
    
    public void setKey(byte[] p_key) {
        this.m_key = p_key;
    }
    
    public Map<byte[], byte[]> getValues() {
        return m_values;
    }
    
    public void addColumn(byte[] p_name, byte[] p_value) {
        if(m_values.put(p_name, p_value) == null) {
            m_keyArrList.add(p_name);
        }
    }
    
    public void removeColumn(byte[] p_name) {
        if(m_values.remove(p_name) != null) {
            m_keyArrList.remove(p_name);
        }
    }
    
    @Override
    @SuppressWarnings("empty-statement")
    public boolean equals(Object p_another) {
        if (!(p_another instanceof EncodedVO)) {
            return false;
        }
        
        if (this == p_another) {
            return true;
        }
        
        EncodedVO other = (EncodedVO) p_another;
        
        EqualsBuilder l_eb = new EqualsBuilder();
        l_eb.append(this.m_key, other.m_key);
        
        if(m_keyArrList.size() != other.m_keyArrList.size()) {
            return false;
        }
        
        Collections.sort(m_keyArrList, BYTE_ARRAY_COMPARATOR);
        Collections.sort(other.m_keyArrList, BYTE_ARRAY_COMPARATOR);
        
        for (int i = 0; i < m_keyArrList.size(); i++) {
            byte[] l_bytes0 = m_keyArrList.get(i);
            byte[] l_bytes1 = other.m_keyArrList.get(i);
            l_eb.append(l_bytes0, l_bytes1).append(m_values.get(l_bytes0), 
                    other.m_values.get(l_bytes1));
        }
        
        return l_eb.isEquals();
    }
    
    @Override
    public int hashCode() {
        HashCodeBuilder l_hb = new HashCodeBuilder();
        l_hb.append(m_key);
        
        Collections.sort(m_keyArrList, BYTE_ARRAY_COMPARATOR);
        
        for (int i = 0; i < m_keyArrList.size(); i++) {
            byte[] l_bytes0 = m_keyArrList.get(i);
            l_hb.append(l_bytes0).append(m_values.get(l_bytes0));
        }
        
        return l_hb.toHashCode();
    }
}
