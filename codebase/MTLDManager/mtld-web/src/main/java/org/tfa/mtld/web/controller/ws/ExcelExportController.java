package org.tfa.mtld.web.controller.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.tfa.mtld.service.bean.CohortBean;
import org.tfa.mtld.service.bean.CriteriaBean;
import org.tfa.mtld.service.bean.CriteriaFormBean;
import org.tfa.mtld.service.constants.TFAConstants;
import org.tfa.mtld.service.services.CohortUpdateService;
import org.tfa.mtld.web.exception.TFAException;
import org.tfa.mtld.web.utils.ColumnMappingConstants;

import com.google.gson.Gson;


/**
 * @author divesh.solanki
 * 
 */
@Controller
public class ExcelExportController {
	Logger logger = Logger.getLogger(ExcelExportController.class);

	
	@Autowired
	public CohortUpdateService cohortUpdateService;

	@RequestMapping(value = "/export/{csvCohortIds}", method = RequestMethod.GET)
	@SuppressWarnings("unchecked")
	public ModelAndView exportData(HttpServletRequest request,
			HttpServletResponse response, @PathVariable String csvCohortIds,HttpSession session)
			throws TFAException {
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			Gson criteriaGson = new Gson();
			CriteriaFormBean criteriaFormBean = criteriaGson.fromJson(
					session.getAttribute(
							TFAConstants.SESSION_CRITERIA_FORM_BEAN_STRING)
							.toString(), CriteriaFormBean.class);
		
			List<CohortBean> cohorts = new ArrayList<CohortBean>();

			String[] cohortIds = csvCohortIds.split(",");
			for (int i = 0; i < cohortIds.length; i++) {
				CohortBean cohort = cohortUpdateService.getCohortDetails(new Integer(
						cohortIds[i].trim()),criteriaFormBean);
				if (cohort != null) {
					cohorts.add(cohort);
				}
			}
			model.put("cohorts", cohorts);
			model.put("criteriaFormBean", criteriaFormBean);
			return new ModelAndView("excelExportDataView", "exportData", model);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new TFAException();
		}

	}

	private void setMTLDHeader(HSSFRow hssfRow) {
		for (int i = 0; i < ColumnMappingConstants.MTLD_HEADER_LABELS.length; i++) {
			hssfRow.createCell(i).setCellValue(
					ColumnMappingConstants.MTLD_HEADER_LABELS[i]);
		}

	}

	private void setCMHeader(HSSFRow hssfRow) {
		for (int i = 0; i < ColumnMappingConstants.CM_HEADER_LABELS.length; i++) {
			hssfRow.createCell(i).setCellValue(
					ColumnMappingConstants.CM_HEADER_LABELS[i]);
		}

	}

}
