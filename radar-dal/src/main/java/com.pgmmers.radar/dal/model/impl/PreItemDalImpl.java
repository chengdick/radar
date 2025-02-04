package com.pgmmers.radar.dal.model.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pgmmers.radar.dal.bean.PageResult;
import com.pgmmers.radar.dal.bean.PreItemQuery;
import com.pgmmers.radar.dal.model.PreItemDal;
import com.pgmmers.radar.dal.util.POVOUtils;
import com.pgmmers.radar.mapper.PreItemMapper;
import com.pgmmers.radar.model.PreItemPO;
import com.pgmmers.radar.vo.model.PreItemVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class PreItemDalImpl implements PreItemDal {

    public static Logger logger = LoggerFactory.getLogger(PreItemDalImpl.class);

    @Autowired
    private PreItemMapper preItemMapper;

    @Override
    public PreItemVO get(Long id) {
        PreItemPO preItem = preItemMapper.selectByPrimaryKey(id);
        if (preItem != null) {
            PreItemVO preItemVO = new PreItemVO();
            BeanUtils.copyProperties(preItem, preItemVO);
            return preItemVO;
        }
        return null;
    }

    @Override
    public PageResult<PreItemVO> query(PreItemQuery query) {
        PageHelper.startPage(query.getPageNo(), query.getPageSize());

        Example example = new Example(PreItemPO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("modelId", query.getModelId());
        if (!StringUtils.isEmpty(query.getPlugin())) {
            criteria.andEqualTo("plugin", query.getPlugin());
        }
        if (query.getStatus() != null) {
            criteria.andEqualTo("status", query.getStatus());
        }
        if (!StringUtils.isEmpty(query.getLabel())) {
            //criteria.andLabelLike(BaseUtils.buildLike(query.getLabel()));
        	criteria.andEqualTo("label", query.getLabel());
        }

        List<PreItemPO> list = preItemMapper.selectByExample(example);
        Page<PreItemPO> page = (Page<PreItemPO>) list;

        List<PreItemVO> listVO = new ArrayList<PreItemVO>();
        for (PreItemPO preItemPO : page.getResult()) {
            PreItemVO preItemVO ;
            preItemVO = POVOUtils.copyFromPreItemPO(preItemPO);
            listVO.add(preItemVO);
        }

        PageResult<PreItemVO> pageResult = new PageResult<>(page.getPageNum(), page.getPageSize(),
                (int) page.getTotal(), listVO);
        return pageResult;
    }

    @Override
    public int save(PreItemVO preItem) {
        PreItemPO preItemPO ;
        preItemPO = POVOUtils.copyFromPreItemVO(preItem);
        Date sysDate = new Date();
        int count = 0;
        if (preItemPO.getId() == null) {
            preItemPO.setCreateTime(sysDate);
            preItemPO.setUpdateTime(sysDate);
            count = preItemMapper.insertSelective(preItemPO);
            preItem.setId(preItemPO.getId());
        } else {
            preItemPO.setUpdateTime(sysDate);
            count = preItemMapper.updateByPrimaryKeySelective(preItemPO);
        }
        return count;
    }

    @Override
    public int delete(Long[] id) {
        Example example = new Example(PreItemPO.class);
        example.createCriteria().andIn("id", Arrays.asList(id));
        int count = preItemMapper.deleteByExample(example);
        // TODO 删除关联子表
        return count;
    }

}
