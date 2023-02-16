package com.sakura.reggieApi.module.setmealmanagement.comtroller;

import com.sakura.reggieApi.common.anno.LogAnnotation;
import com.sakura.reggieApi.common.utils.TokenUtils;
import com.sakura.reggieApi.module.setmealmanagement.pojo.Setmeal;
import com.sakura.reggieApi.module.setmealmanagement.service.SetmealService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sakura
 * @className SetmealController
 * @createTime 2023/2/13
 */
@RestController
@RequestMapping("/backend/setmeal")
public class SetmealController {

    private static final String HERDER_TOKEN_KEY = TokenUtils.REQUEST_HEADER_TOKEN_KEY;

    @Resource
    SetmealService setmealService;

    @LogAnnotation("更新套餐详情信息")
    @PreAuthorize("hasAnyRole('ADMIN','SUPADMIN')")
    @PutMapping("/update")
    public String doUpdateSetmeal(@RequestHeader(HERDER_TOKEN_KEY) String token,
                                  @RequestBody Setmeal setmealReq) {
        return setmealService.updateSermealDetail(token, setmealReq);
    }

    @LogAnnotation("查询套餐详情")
    @PreAuthorize("hasAnyRole('ADMIN','SUPADMIN', 'USER')")
    @GetMapping("/detail/{name}")
    public String doSetmealDetail(@RequestHeader(HERDER_TOKEN_KEY) String token,
                                  @PathVariable("name") String name) {
        return setmealService.queryDetail(token, name);
    }

    @LogAnnotation("添加套餐")
    @PreAuthorize("hasAnyRole('ADMIN','SUPADMIN')")
    @PostMapping("/add")
    public String doAddSetmeal(@RequestHeader(HERDER_TOKEN_KEY) String token,
                               @RequestBody Setmeal setmealReq) {
        return setmealService.addSetmeal(token, setmealReq);
    }


    @LogAnnotation("批量 更新套餐的售卖状态以及删除套餐")
    @PreAuthorize("hasAnyRole('ADMIN','SUPADMIN')")
    @PutMapping("/batchUpdate")
    public String doBatchSimpleUpdate(@RequestHeader(HERDER_TOKEN_KEY) String token,
                                      @RequestBody List<Setmeal> setmealList) {
        return setmealService.batchUpdate(token, setmealList);
    }

    @LogAnnotation("更新套餐的售卖状态以及删除套餐")
    @PreAuthorize("hasAnyRole('ADMIN','SUPADMIN')")
    @PutMapping("/simpleUpdate")
    public String doSimpleUpdate(@RequestHeader(HERDER_TOKEN_KEY) String token,
                                 @RequestBody Setmeal setmeal) {
        return setmealService.simpleUpdate(token, setmeal);
    }

    @LogAnnotation("分页查询套餐列表")
    @PreAuthorize("hasAnyRole('ADMIN','SUPADMIN')")
    @GetMapping("/list/{curPage}/{name}")
    public String doListPage(@RequestHeader(HERDER_TOKEN_KEY) String token,
                             @PathVariable("curPage") Integer curPage,
                             @PathVariable("name") String name) {
        return setmealService.listPage(token, curPage, name);
    }

}
