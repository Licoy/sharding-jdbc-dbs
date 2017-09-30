package cn.licoy.dbs.repository;

import cn.licoy.dbs.entity.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author licoy.cn
 * @version 2017/9/30
 */
@Repository
public interface OrderRepository extends CrudRepository<Order,Long> {
}
