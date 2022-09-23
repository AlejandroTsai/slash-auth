package com.heycine.slash.auth.business.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heycine.slash.auth.business.entity.OauthRefreshTokenEntity;
import com.heycine.slash.auth.business.mapper.OauthRefreshTokenMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Alikes
 * @since 2022-01-27
 */
@Service
public class OauthRefreshTokenRepository extends ServiceImpl<OauthRefreshTokenMapper, OauthRefreshTokenEntity> {

}
