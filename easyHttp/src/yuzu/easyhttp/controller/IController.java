package yuzu.easyhttp.controller;

import yuzu.easyhttp.http.IHttpRequest;
import yuzu.easyhttp.http.IHttpResponse;

/** 控制器 */
public interface IController {

	/**
	 * 处理
	 * 
	 * @param request
	 *            http请求
	 * @param response
	 *            http答复
	 * @return 返回的内容
	 */
	public Object handle(IHttpRequest request, IHttpResponse response);

}
