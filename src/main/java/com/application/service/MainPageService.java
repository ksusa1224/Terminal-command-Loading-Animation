package com.application.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.application.controller.dao.QAPlusDao;
import com.application.model.dao.MondaiModel;
import com.application.model.dao.QAModel;
import com.application.model.dao.QAPlusModel;
import com.application.model.dao.SeitouModel;
import com.common.StopWatch;
import com.common.Util;

@Service
public class MainPageService {

	/**
	 * 
	 * @return
	 */
	public String generate_qa_html(List<QAPlusModel> qa_plus_list, String owner_db)
	{
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();

		StringBuffer qa_html = new StringBuffer();
		
		Map<String, Integer> qa_id_with_date = new HashMap<String, Integer>();
		qa_id_with_date = get_qa_count_per_date(qa_plus_list);
		System.out.println("サイズ："+qa_id_with_date.size());
		
		for (QAPlusModel qa_plus : qa_plus_list)
		{
			List<MondaiModel> mondai_list = new ArrayList<MondaiModel>();
			mondai_list = qa_plus.getMondai_list();
						
			/**
			 * 問題HTML
			 */
			List<String> q_html = new ArrayList<String>();
			for (int i = 0; i < mondai_list.size(); i++)
			{
				String mondai = mondai_list.get(i).getQ_parts_text();
				String q_lang = mondai_list.get(i).getLanguage();
				String html = "<span id='" + mondai_list.get(i).getQ_id() + "' class='q' data-language='" + q_lang + "'>" + mondai + "</span>";			
				q_html.add(html);
			}
			
			/**
			 * 正答HTML
			 */
			List<SeitouModel> seitou_list = new ArrayList<SeitouModel>();
			seitou_list = qa_plus.getSeitou_list();
			
			List<String> a_html = new ArrayList<String>();
			for (int i = 0; i < seitou_list.size(); i++)
			{
				String seitou = seitou_list.get(i).getSeitou();
//				KaitouDao kaitou_dao = new KaitouDao();
//				int opacity = kaitou_dao.is_seikai(owner_db, seitou_list.get(i).getS_id());
				int opacity = seitou_list.get(i).getSeikai_flg();
				String mouseout = "";
				String checked = "";
				String a_lang = seitou_list.get(i).getLanguage();
				if (opacity == 0)
				{
					mouseout = "onmouseout='this.style.opacity=0'";
				}
				else
				{
					checked = "<img src='../img/check.png' class='check' />";					
				}
				String html = "<span id='" + seitou_list.get(i).getS_id() + "' class='a' style='opacity:" + opacity + "' onmouseover='this.style.opacity=1' " + mouseout + " onclick='change_seitou_color(this)' data-language='" + a_lang + "'>" + checked + seitou + "</span>";				
				a_html.add(html);
			}			
			
			if (seitou_list.size() > 0 && mondai_list.size() > 0)
			{
				String yomudake = "";
				for (SeitouModel seitou : seitou_list)
				{
					if (seitou.getSeitou().equals("読んだ"))
					{
						yomudake = " data-yomudake='true'";
					}
				}
				if (qa_id_with_date.containsKey(qa_plus.getQa().getQa_id()))
				{
					Util util = new Util();
					String sakuseibi_yyyyMMdd = util.getDay(qa_plus.getQa().getCreate_timestamp());
					String sakuseibi_MMdd = sakuseibi_yyyyMMdd.split("/")[1] + "/" + sakuseibi_yyyyMMdd.split("/")[2];
					int qa_cnt_per_date = qa_id_with_date.get(qa_plus.getQa().getQa_id());
					qa_html.append("<span id='" + sakuseibi_yyyyMMdd + "' class='date'>" + sakuseibi_MMdd + "（" + qa_cnt_per_date + "問）" + "</span>");					
				}
				qa_html.append("<span id='" + qa_plus.getQa().getQa_id() + "' class='qa' onmouseover='qa_mouseover(this)'" + yomudake + ">");
			}

			for (int i = 0; i < (mondai_list.size() + seitou_list.size()); i++)
			{
				if (qa_plus.getQa().getIs_start_with_q() == 1)
				{
					if (i < mondai_list.size())
					{
						qa_html.append(q_html.get(i));
					}
					if (i < seitou_list.size())
					{
						qa_html.append(a_html.get(i));
					}
				}
				else
				{
					if (i < seitou_list.size())
					{
						qa_html.append(a_html.get(i));
					}
					if (i < mondai_list.size())
					{
						qa_html.append(q_html.get(i));
					}
				}
			}
			if (seitou_list.size() > 0 && mondai_list.size() > 0)
			{
				qa_html.append("</span>");
			}
		}
	    stopwatch.stop(new Object(){}.getClass().getEnclosingMethod().getName());
		
		return qa_html.toString();
	}
	
	/**
	 * 
	 * @param owner_db
	 * @return
	 */
	public List<QAPlusModel> select_qa_plus(String owner_db, Integer limit, Integer offset) {
		QAPlusDao qa_plus_dao = new QAPlusDao();
		List<QAPlusModel> qa_plus_list = new ArrayList<QAPlusModel>();
		qa_plus_list = qa_plus_dao.select_qa_plus_map(owner_db, qa_plus_list, limit, offset);
		return qa_plus_list;
	}		
	
	/**
	 * 
	 * @param owner_db
	 * @param husen_name
	 * @return
	 */
	public List<QAPlusModel> select_qa_plus_by_tag(String owner_db, String husen_names, Integer limit, Integer offset) {
		QAPlusDao qa_plus_dao = new QAPlusDao();
		List<QAPlusModel> qa_plus_list = new ArrayList<QAPlusModel>();
		qa_plus_list = qa_plus_dao.select_qa_plus_list(owner_db, qa_plus_list, husen_names, limit , offset);
		return qa_plus_list;
	}	

	public Map<String, Integer> get_qa_count_per_date(List<QAPlusModel> qa_plus_list) {
		List<QAModel> qa_list = new ArrayList<QAModel>();
		for (QAPlusModel qa_plus : qa_plus_list)
		{
			QAModel qa = new QAModel();
			qa.setQa_id(qa_plus.getQa().getQa_id());
			Util util = new Util();
			String sakuseibi = util.getDay(qa_plus.getQa().getCreate_timestamp());
			qa.setCreate_timestamp(sakuseibi);
			//System.out.println("作成日"+sakuseibi);
			qa_list.add(qa);
		}
		
		Map<String, List<QAModel>> qa_id_per_date = 
				qa_list
				.stream()
				.filter(p -> p.getCreate_timestamp() != null)
				.collect(Collectors.groupingBy(QAModel::getCreate_timestamp));
		
		Map<String, Integer> qa_count_per_date = new HashMap<String, Integer>();
		for (Map.Entry<String, List<QAModel>> entry : qa_id_per_date.entrySet())
		{
			qa_count_per_date.put(entry.getValue().get(0).getQa_id(), 
							entry.getValue().size());
		}
				
		return qa_count_per_date;
	}
}
