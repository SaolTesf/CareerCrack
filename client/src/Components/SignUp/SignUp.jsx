import React from 'react';
import { FaUser, FaLock } from "react-icons/fa";
import { MdEmail } from 'react-icons/md';

export const SignUp = () => {
  return (
    <div className="form">
      <div className="input">
        <MdEmail />
        <input 
          id="email"
          type="email"
          placeholder="email"
        />
      </div>
      <div className="input">
        <FaLock />
        <input 
          id="password"
          type="password"
          placeholder="password"
        />
      </div>
      <div className="input">
        <FaUser />
        <input 
          id="username"
          type="username"
          placeholder="username"
        />
      </div>
    </div>
  )
}
