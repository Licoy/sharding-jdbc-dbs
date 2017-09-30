package cn.licoy.dbs.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author licoy.cn
 * @version 2017/9/30
 */
@Data
@Entity
@Table(name = "t_order")
public class Order implements Serializable {

    @Id
    private Long orderId;

    private Long userId;

    private static final long serialVersionUID = 1L;

}
