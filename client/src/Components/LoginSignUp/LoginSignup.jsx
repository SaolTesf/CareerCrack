import React from 'react'
import './LoginSignup.css'

import { FaUser } from "react-icons/fa";
import { FaLock } from "react-icons/fa";


export const LoginSignup = () => {
  return (
    <div className='container'>
      <div className='header'>
        <div className='text'>Signup</div>
        <div className='underline'></div>
      </div>
      <div className='form'>
        <div className='input'>
          <FaUser />
          <input id='username-email' type='username/email' placeholder='username/email'/>
        </div>
        <div className='input'>
          <FaLock />
          <input id='password' type='password' placeholder='password'/>
        </div>
      </div>
      <div className="forgot-password">Forgot Password? <span>Click Here!</span></div>
      <div className='submit-container'>
        <div className='submit'>Sign Up</div>
        <div className='submit'>Login</div>
      </div>
    </div>
  )
}
