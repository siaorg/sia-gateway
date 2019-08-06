export const RegExpressions = {
  /**
   * Common validation RegExp with error information
   */
  userName: {
    reg: /^(?!^[ ]+$)[\u4e00-\u9fa5_a-zA-Z0-9_ ]{2,30}$/,
    error_msg: 'Name limit 2-30 bit characters, can contain letters (case sensitive), Numbers, underline, chinese, space' // Name must be string with 2 to 30 characters!
  },
  accountName: {
    reg: /^[a-zA-Z0-9_]{3,15}$/,
    error_msg: 'Account Name limit 3-15 bit characters, can contain letters (case sensitive), Numbers, underline'
  },
  password: {
    reg: /^[a-zA-Z0-9_]{6,16}$/,
    error_msg: 'Password limit 6-16 bit characters, can contain letters (case sensitive), Numbers, underline'
  },
  verificationCode: {
    reg: /^[0-9]{4}$/,
    error_msg: 'The verification code limits 4 digits!'
  },
  email: {
    reg: /^[A-Za-z\d]+([-_.][A-Za-z\d]+)*@([A-Za-z\d]+[-.])+[A-Za-z\d]{2,4}$/,
    error_msg: 'Email address format error!'
  },
  phoneNumber: {
    reg: /^\d{7,11}$/,
    error_msg: 'Phone number error!'
  },
  pptPrincipalID: {
    reg: /^(\d{15}$|^\d{18}$|^\d{17}(\d|X|x))$/,
    error_msg: '身份证号输入有误！'
  },
  // 验证登录账号，只能为手机号码或者邮箱
  account: {
    reg: /^1[34578]\d{9}$|^[A-Za-z\d]+([-_.][A-Za-z\d]+)*@([A-Za-z\d]+[-.])+[A-Za-z\d]{2,4}$/,
    error_msg: 'Incorrect account'
  },
  age: {
    reg: /^[1-9][0-9]{0,2}$/,
    error_msg: 'Age must be positive integers with 3 digits number!'
  },
  responsibilities: {
    reg: /^(?=.*?\S)[\s\S]{2,30}$/g,
    error_msg: 'Responsibilities must be string with 2 to 30 characters!'
  },
  jobDesc: {
    reg: /^(?=.*?\S)[\s\S]{2,500}$/g,
    error_msg: 'Job description must be string with 2 to 500 characters!'
  },
  tagName: {
    reg: /^(?!^[ ]+$)[\u4e00-\u9fa5_a-zA-Z0-9_ ]{1,30}$/,
    error_msg: 'tag must be string with 1 to 30 characters!'
  },
  eventName: {
    reg: /^(?=.*?\S)[\s\S]{2,50}$/g,
    error_msg: 'Name must be string with 2 to 50 characters!'
  },
  eventDesc: {
    reg: /^[^]{0,500}$/,
    error_msg: 'No more than 500 characters!'
  },
  eventLocation: {
    reg: /^(?=.*?\S)[\s\S]{2,50}$/g,
    error_msg: 'Location must be string with 2 to 50 characters!'
  },
  spaceName: {
    reg: /^(?=.*?\S)[\s\S]{2,100}$/g,
    error_msg: 'Name must be string with 2 to 100 characters!'
  },
  spaceLocation: {
    reg: /^(?=.*?\S)[\s\S]{2,100}$/g,
    error_msg: 'Location must be string with 2 to 100 characters!'
  },
  roomTypeName: {
    reg: /^(?=.*?\S)[\s\S]{2,100}$/g,
    error_msg: 'Name must be string with 2 to 100 characters!'
  }
}
