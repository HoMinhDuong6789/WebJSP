package ued.udn.vn.core.data.dao;

import java.io.Serializable;
import java.util.List;

public interface GenericDao <ID extends Serializable, T>{
    // de deex dang mowr rongj class sau nay hon

    List<T> findAll(); //ko khai bao acesset motify thi mawjc dinh la public
    T update(T entity);
    void save(T entity);
    T findById(ID id);
    Object[] findByProperty(String property, Object value, String sortExpression, String sortDirection);
    Integer delete (List <ID> ids);

}
