import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import React from 'react';

const Footer: React.FC = () => {
  const defaultMessage = "法外狂徒出品"
  return (
    <DefaultFooter
      copyright={`${new Date().getFullYear()} ${defaultMessage}`}
      style={{
        background: 'none',
      }}
      links={[
        {
          key: 'Ant Design Pro',
          title: '百度',
          href: 'https://www.baidu.com',
          blankTarget: true,
        },
        {
          key: 'github',
          title: <><GithubOutlined /> 编程大白 </>,
          href: 'https://github.com',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
