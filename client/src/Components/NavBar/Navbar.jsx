import { React, useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { FaHome } from "react-icons/fa";

export const Navbar = () => {
  return (
    <nav className='navbar'>
      <Link to={"/home"}>
        <span>
          <span>
            <FaHome />
          </span>
        </span>
      </Link>
      <Link to={"/home"}>
        <span>
          <span>
            Home
          </span>
        </span>
      </Link>
      <Link to={"/problems"}>
        <span>
          <span>
            Problems
          </span>
        </span>
      </Link>
      <Link to={"/applications"}>
        <span>
          <span>
            Applications
          </span>
        </span>
      </Link>
      <Link to={"/settings"}>
        <span>
          <span>
            Settings
          </span>
        </span>
      </Link>
    </nav>
  )

}
