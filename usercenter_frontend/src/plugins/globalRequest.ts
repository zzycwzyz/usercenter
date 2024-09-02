/**
 * request 网络请求工具
 * 更详细的 api 文档: https://github.com/umijs/umi-request
 */
import {extend, RequestMethod} from 'umi-request';
import {message} from "antd";
import {history} from "@@/core/history";
import {stringify} from "querystring";

/**
 * 配置request请求时的默认参数
 */
const request: RequestMethod<true> = extend({
  credentials: 'include', // 默认请求是否带上cookie
  // prefix: process.env.NODE_ENV === 'production' ? 'http://124.70.90.198:80/api' : 'http://localhost:8000/api',
  prefix: process.env.NODE_ENV === 'production' ? 'https://usercenter-frontend-115107-5-1315421160.sh.run.tcloudbase.com/api' : 'http://localhost:8000/api',
});

/**
 * 所有请求拦截器
 */
request.interceptors.request.use((url, options): any => {
  console.log(`do request url = ${url}`)
  return {
    url,
    options: {
      ...options,
      headers: {},
    },
  };
});

/**
 * 所有响应拦截器
 */
request.interceptors.response.use(async (response): Promise<any> => {
  const res = await response.clone().json();
  if (res.code === 0) {
    return res.data;
  }
  if (res.code === 40100) {
    history.replace({
      pathname: '/user/login',
      search: stringify({
        redirect: location.pathname,
      }),
    });
    message.error('未登录');
  } else {
    message.error(res.description);
  }
  return res.data;
});

export default request;
