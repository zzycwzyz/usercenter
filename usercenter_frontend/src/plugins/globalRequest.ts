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
  baseUrl: '/api',
  credentials: 'include', // 默认请求是否带上cookie
  // prefix: process.env.NODE_ENV === 'production' ? 'TODO' : undefined
  // requestType: 'form',
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
request.interceptors.response.use(async (response,options): Promise<any> => {
  const res = await response.clone().json();
  console.log('res', res);
  if (response.code === 0) {
    return response.data;
  }
  if (response.code === 40100) {
    message.error('未登录');
    history.replace({
      pathname: '/user/login',
      search: stringify({
        redirect: location.pathname,
      }),
    });
  } else {
    message.error(response.description)
  }
});


export default request;
