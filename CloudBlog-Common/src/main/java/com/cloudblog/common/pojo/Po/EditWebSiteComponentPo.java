package com.cloudblog.common.pojo.Po;

import com.cloudblog.common.pojo.DoMain.SiteContent;
import lombok.Data;

import java.util.List;

@Data
public class EditWebSiteComponentPo {

    private WebSiteComponentPo webSiteComponentPo;

    private List<SiteContent> siteContentList;
}
