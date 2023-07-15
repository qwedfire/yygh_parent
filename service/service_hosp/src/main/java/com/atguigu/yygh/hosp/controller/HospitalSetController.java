package com.atguigu.yygh.hosp.controller;

import com.atguigu.common.exception.YyghException;
import com.atguigu.common.result.Result;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@Api(tags = "醫院設置管理")
@RestController
@RequestMapping("admin/hosp/hospitalSet")
public class HospitalSetController {
    @Autowired
    private HospitalSetService hospitalSetService;

    //1.查詢醫院設置表所有消息
    @ApiOperation(value = "獲取所有醫院設置")
    @GetMapping("findAll")
//    public List<HospitalSet> findAllHospitalSet(){
    public Result findAllHospitalSet(){
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
//        return list;
    }

    //2.邏輯刪除醫院設置
    @ApiOperation(value = "邏輯刪除醫院設置")
    @DeleteMapping("{id}")
//    public boolean removeHospSet(@PathVariable Long id){
    public Result removeHospSet(@PathVariable Long id){
        boolean flag=hospitalSetService.removeById(id);
//        return flag;
        return flag?Result.ok():Result.fail();
    }

    //3.條件查詢帶分頁
    //RequestBody required=false 通過json傳遞數據 但是不能使用get 必須改為post傳遞
    @PostMapping("findPageHospSet/{current}/{limit}")
    public Result findPageHospSet(@PathVariable long current,
                                  @PathVariable long limit,
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){
        //創建page對象 傳遞當前頁 每頁紀錄數
        Page<HospitalSet> page=new Page<>(current,limit);
        //構造條件
        QueryWrapper<HospitalSet> queryWrapper=new QueryWrapper<>();
        String hoscode = hospitalSetQueryVo.getHoscode(); //醫院編號
        String hosname = hospitalSetQueryVo.getHosname(); //醫院名稱
        if(!StringUtils.isEmpty(hosname)){
            queryWrapper.like("hosname",hospitalSetQueryVo.getHosname());
        }
        if(!StringUtils.isEmpty(hoscode)){
            queryWrapper.eq("hoscode",hospitalSetQueryVo.getHoscode());
        }
        //調用方法實現分頁查詢
        Page<HospitalSet>pageHospitalSet=hospitalSetService.page(page,queryWrapper);

        return Result.ok(pageHospitalSet);
    }
    //4.添加醫院設置
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet){
        //設置狀態1使用0不能使用
        hospitalSet.setStatus(1);
        //簽名秘鑰
        Random random=new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));
        boolean save=hospitalSetService.save(hospitalSet);
        return save?Result.ok():Result.fail();
    }
    //5.根據id查詢
    @GetMapping("getHospSet/{id}")
    public Result getHospSet(@PathVariable Long id){
        HospitalSet hospitalSet=hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }
    //6.修改醫院設置
    @PostMapping("updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet){
        boolean flag = hospitalSetService.updateById(hospitalSet);
        return flag?Result.ok():Result.fail();
    }
    //7.批量刪除醫院設置
    @DeleteMapping("batchRemove")
    public Result batchRemoveHospitalSet(@RequestBody List<Long> idList){
        boolean flag = hospitalSetService.removeByIds(idList);
        return flag?Result.ok():Result.fail();
    }
    //8.醫院設置鎖定和解鎖 欄位 status0解鎖 1鎖定
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id,
                                  @PathVariable Integer status){
        //根據id查詢醫院設置信息
        HospitalSet hospitalSet=hospitalSetService.getById(id);
        //設置狀態
        hospitalSet.setStatus(status);
        //調用方法
        hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }
    //9.發送簽名秘鑰
    @PutMapping("sendKey/{id}")
    public Result lockHospitalSet(@PathVariable Long id){
        try{
//        測試全局異常用
//        int a=1/0;
        }catch (Exception e){
            throw new YyghException("失敗",201);
        }
        HospitalSet hospitalSet=hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        //TODO 發送短信
        return Result.ok();
    }
}
