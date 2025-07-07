import React from 'react'
import './LoginSignup.css'

import { CiUser } from "react-icons/ci";
import { RiLockPasswordLine } from "react-icons/ri";


export const LoginSignup = () => {
  return (
    <div className='container'>
      <div className='header'>
        <div className='text'>Signup</div>
        <div className='underline'></div>
        <div className='form'>
          <div className='input'>
            <CiUser />
            <input type='username/email' />
          </div>
          <div className='input'>
            <RiLockPasswordLine />
            <input type='password' />
          </div>
        </div>
      </div>
    </div>
  )
}
