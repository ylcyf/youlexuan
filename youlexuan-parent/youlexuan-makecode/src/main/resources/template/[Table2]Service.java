package [package].service;
import java.util.List;
import [path_1].[path_2].pojo.Tb[Table2];

import com.offcn.entity.PageResult;
/**
 * [comment]服务层接口
 * @author Administrator
 *
 */
public interface [Table2]Service {

	/**
	 * 返回全部列表
	 * @return
	 */
	List<Tb[Table2]> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	PageResult findPage(int pageNum,int pageSize);
	
	
	/**
	 * 增加
	*/
	void add(Tb[Table2] [table2]);
	
	
	/**
	 * 修改
	 */
	void update(Tb[Table2] [table2]);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	Tb[Table2] findOne([PrimaryKeyType] [PrimaryKeyName]);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	void delete([PrimaryKeyType] [] [PrimaryKeyName]s);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageResult findPage(Tb[Table2] [table2], int pageNum,int pageSize);
	
}
