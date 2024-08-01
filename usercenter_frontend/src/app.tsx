import {AvatarDropdown, AvatarName, Footer, Question} from '@/components';
import {currentUser, currentUser as queryCurrentUser} from '@/services/ant-design-pro/api';
import {LinkOutlined} from '@ant-design/icons';
import type {Settings as LayoutSettings} from '@ant-design/pro-components';
import {SettingDrawer} from '@ant-design/pro-components';
import {history, Link, RequestConfig, RunTimeLayoutConfig} from '@umijs/max';
import defaultSettings from '../config/defaultSettings';
import {AxiosResponse} from "axios";
import {message} from "antd";
import {stringify} from "querystring";

const isDev = process.env.NODE_ENV === 'development';
const loginPath = '/user/login';
const whiteList = [loginPath, '/user/register'];
type WithPromise<T> = T | Promise<T>;


/**
 * @see  https://umijs.org/zh-CN/plugins/plugin-initial-state
 * */
export async function getInitialState(): Promise<{
  settings?: Partial<LayoutSettings>;
  currentUser?: API.CurrentUser;
  loading?: boolean;
  fetchUserInfo?: () => Promise<API.CurrentUser | undefined>;
}> {
  console.log('初始化');
  const fetchUserInfo = async () => {
    try {
      console.log('获取用户信息');
      return await queryCurrentUser();
    } catch (error) {
      history.push(loginPath);
    }
    return undefined;
  };
  // 如果不是登录和注册页面，执行
  console.log('history', history);
  const {location} = history;
  if (!whiteList.includes(location.pathname)) {
    const currentUser = await fetchUserInfo();
    return {
      // @ts-ignore
      fetchUserInfo,
      // @ts-ignore
      currentUser,
      settings: defaultSettings as Partial<LayoutSettings>,
    };
  }
  return {
    settings: defaultSettings as Partial<LayoutSettings>,
  };
}

export const request: RequestConfig = {
  baseURL: '/api',
  timeout: 30000,
  // 自定义全局响应拦截器
  responseInterceptors: [
    // @ts-ignore
    function <T extends AxiosResponse<T, any> = any>(response: AxiosResponse<T>): WithPromise<AxiosResponse<T>> {
      if("code" in response.data && "message" in response.data && "description" in response.data){
        console.log('全局响应拦截器', response);
        if (response.data.code === 0) {
          return Promise.resolve(response.data);
        }
        if (response.data.code === 40100) {
          message.error('请先登录');
          history.replace({
            pathname: '/user/login',
            search: stringify({
              redirect: location.pathname,
            }),
          });
          return Promise.reject(new Error('未登录'));
        } else {
          console.log('全局响应拦截器', response.data.description);
          // @ts-ignore
          return Promise.reject(new Error(response.data.description || '请求出错'));
        }
      }
    }
  ]
};

// ProLayout 支持的api https://procomponents.ant.design/components/layout
export const layout: RunTimeLayoutConfig = ({initialState, setInitialState}) => {
  return {
    actionsRender: () => [<Question key="doc"/>],
    avatarProps: {
      src: initialState?.currentUser?.avatarUrl,
      title: <AvatarName/>,
      render: (_, avatarChildren) => {
        return <AvatarDropdown>{avatarChildren}</AvatarDropdown>;
      },
    },
    waterMarkProps: {
      content: initialState?.currentUser?.username,
    },
    footerRender: () => <Footer/>,
    onPageChange: () => {},
    bgLayoutImgList: [
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/D2LWSqNny4sAAAAAAAAAAAAAFl94AQBr',
        left: 85,
        bottom: 100,
        height: '303px',
      },
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/C2TWRpJpiC0AAAAAAAAAAAAAFl94AQBr',
        bottom: -68,
        right: -45,
        height: '303px',
      },
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/F6vSTbj8KpYAAAAAAAAAAAAAFl94AQBr',
        bottom: 0,
        left: 0,
        width: '331px',
      },
    ],
    links: isDev
      ? [
        <Link key="openapi" to="/umi/plugin/openapi" target="_blank">
          <LinkOutlined/>
          <span>OpenAPI 文档</span>
        </Link>,
      ]
      : [],
    menuHeaderRender: undefined,
    // 自定义 403 页面
    // unAccessible: <div>unAccessible</div>,
    // 增加一个 loading 的状态
    childrenRender: (children) => {
      // if (initialState?.loading) return <PageLoading />;
      return (
        <>
          {children}
          {isDev && (
            <SettingDrawer
              disableUrlParams
              enableDarkTheme
              settings={initialState?.settings}
              onSettingChange={(settings) => {
                setInitialState((preInitialState) => ({
                  ...preInitialState,
                  settings,
                }));
              }}
            />
          )}
        </>
      );
    },
    ...initialState?.settings,
  };
};
