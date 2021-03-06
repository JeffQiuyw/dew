package com.tairanchina.csp.dew.auth.service


import com.ecfront.dew.common.{$, Page, Resp}
import com.tairanchina.csp.dew.Dew
import com.tairanchina.csp.dew.auth.AuthConfig
import com.tairanchina.csp.dew.auth.domain._
import com.tairanchina.csp.dew.auth.dto.management._
import com.tairanchina.csp.dew.auth.repository._
import com.typesafe.scalalogging.LazyLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ManagementService @Autowired()(
                                      val tenantRepository: TenantRepository,
                                      val resourceRepository: ResourceRepository,
                                      val roleRepository: RoleRepository,
                                      val identRepository: IdentRepository,
                                      val accountRepository: AccountRepository,
                                      val authConfig: AuthConfig
                                    ) extends LazyLogging {

  def publish(): Resp[Void] = {
    Dew.cluster.mq.publish(AuthConfig.MQ_PUB_ALL_CACHE, "")
    Resp.success(null)
  }

  //================================= Tenant =================================
  @Transactional
  def addTenant(req: TenantAddReq): Resp[TenantResp] = {
    val tenant: Tenant = req
    tenant.id = $.field.createShortUUID()
    tenant.secret = $.security.digest.digest($.field.createUUID(), "MD5")
    tenant.accessToken = ""
    tenant.enabled = req.enabled
    val saved = tenantRepository.save(tenant)
    Resp.success(saved)
  }

  @Transactional
  def modifyTenant(id: String, req: TenantModifyReq): Resp[Void] = {
    val tenant = TenantModifyReq.convert(req, tenantRepository.findOne(id))
    tenant.name = req.name
    tenant.enabled = req.enabled
    tenantRepository.save(tenant)
    Resp.success(null)
  }

  @Transactional(readOnly = true)
  def getTenant(id: String): Resp[TenantResp] = {
    Resp.success(tenantRepository.findOne(id))
  }

  @Transactional(readOnly = true)
  def pagingTenants(pageNumber: Int, pageSize: Int): Resp[Page[TenantResp]] = {
    Resp.success(tenantRepository.findAll(new PageRequest(pageNumber, pageSize)))
  }

  @Transactional
  def disableTenant(id: String): Resp[Void] = {
    tenantRepository.disable(id)
    Resp.success(null)
  }

  @Transactional
  def enableTenant(id: String): Resp[Void] = {
    tenantRepository.enable(id)
    Resp.success(null)
  }

  //================================= Resource =================================
  @Transactional
  def addResource(req: ResourceAddReq): Resp[ResourceResp] = {
    val resource: Resource = req
    val saved = resourceRepository.save(resource)
    Resp.success(saved)
  }

  @Transactional
  def modifyResource(id: Int, req: ResourceModifyReq): Resp[Void] = {
    val resource = ResourceModifyReq.convert(req, resourceRepository.findOne(id))
    resourceRepository.save(resource)
    Resp.success(null)
  }

  @Transactional(readOnly = true)
  def getResource(id: Int): Resp[ResourceResp] = {
    Resp.success(resourceRepository.findOne(id))
  }

  @Transactional(readOnly = true)
  def pagingResources(pageNumber: Int, pageSize: Int): Resp[Page[ResourceResp]] = {
    Resp.success(resourceRepository.findAll(new PageRequest(pageNumber, pageSize)))
  }

  //================================= Role =================================
  @Transactional
  def addRole(req: RoleAddReq): Resp[RoleResp] = {
    val role: Role = req
    role.id = $.field.createShortUUID()
    val saved = roleRepository.save(role)
    Resp.success(saved)
  }

  @Transactional
  def modifyRole(id: String, req: RoleModifyReq): Resp[Void] = {
    val role = RoleModifyReq.convert(req, roleRepository.findOne(id))
    roleRepository.save(role)
    Resp.success(null)
  }

  @Transactional(readOnly = true)
  def getRole(id: String): Resp[RoleResp] = {
    Resp.success(roleRepository.findOne(id))
  }

  @Transactional(readOnly = true)
  def pagingRoles(pageNumber: Int, pageSize: Int): Resp[Page[RoleResp]] = {
    Resp.success(roleRepository.findAll(new PageRequest(pageNumber, pageSize)))
  }

  @Transactional
  def disableRole(id: String): Resp[Void] = {
    roleRepository.disable(id)
    Resp.success(null)
  }

  @Transactional
  def enableRole(id: String): Resp[Void] = {
    roleRepository.enable(id)
    Resp.success(null)
  }

  //================================= Account =================================
  @Transactional
  def addAccount(req: AccountAddReq): Resp[AccountResp] = {
    val account: Account = req
    account.id = $.field.createUUID()
    account.password = Account.generatePassword(req.password, authConfig.passwordSalt)
    val saved = accountRepository.save(account)
    Resp.success(saved)
  }

  @Transactional
  def modifyAccount(id: String, req: AccountModifyReq): Resp[Void] = {
    val account = AccountModifyReq.convert(req, accountRepository.findOne(id))
    if (req.password != null && req.password.nonEmpty) {
      account.password = Account.generatePassword(account.password, authConfig.passwordSalt)
    } else {
      account.password = accountRepository.findOne(id).password
    }
    accountRepository.save(account)
    Resp.success(null)
  }

  @Transactional(readOnly = true)
  def getAccount(id: String): Resp[AccountResp] = {
    Resp.success(accountRepository.findOne(id))
  }

  @Transactional(readOnly = true)
  def pagingAccounts(pageNumber: Int, pageSize: Int): Resp[Page[AccountResp]] = {
    Resp.success(accountRepository.findAll(new PageRequest(pageNumber, pageSize)))
  }

  @Transactional
  def disableAccount(id: String): Resp[Void] = {
    accountRepository.disable(id)
    Resp.success(null)
  }

  @Transactional
  def enableAccount(id: String): Resp[Void] = {
    accountRepository.enable(id)
    Resp.success(null)
  }

  //================================= Ident =================================
  @Transactional
  def addIdent(req: IdentAddReq): Resp[IdentResp] = {
    val ident: Ident = req
    val saved = identRepository.save(ident)
    Resp.success(saved)
  }

  @Transactional
  def modifyIdent(id: Int, req: IdentModifyReq): Resp[Void] = {
    val ident = IdentModifyReq.convert(req, identRepository.findOne(id))
    identRepository.save(ident)
    Resp.success(null)
  }

  @Transactional(readOnly = true)
  def getIdent(id: Int): Resp[IdentResp] = {
    Resp.success(identRepository.findOne(id))
  }

  @Transactional(readOnly = true)
  def pagingIdents(pageNumber: Int, pageSize: Int): Resp[Page[IdentResp]] = {
    Resp.success(identRepository.findAll(new PageRequest(pageNumber, pageSize)))
  }

  @Transactional
  def disableIdent(id: Int): Resp[Void] = {
    identRepository.disable(id)
    Resp.success(null)
  }

  @Transactional
  def enableIdent(id: Int): Resp[Void] = {
    identRepository.enable(id)
    Resp.success(null)
  }

}
