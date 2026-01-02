import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { FaHome, FaBriefcase, FaClipboardList, FaCog, FaSearch } from "react-icons/fa";
import logo from './../Assets/CareerCrack-logo530.png'
import open from './../Assets/ui/open.png';
import minimze from './../Assets/ui/minimize.png';
import close from './../Assets/ui/close.png';
import openHover from './../Assets/ui/open-hover.png';
import minimzeHover from './../Assets/ui/minimize-hover.png';
import closeHover from './../Assets/ui/close-hover.png';
import './Navbar.css';

export const Navbar = ({ isMinimized, setIsMinimized }) => {
  const [isHovered, setIsHovered] = useState(false);
  
  const location = useLocation();
  let selectedTab = location.pathname;

  let navbarImage = (isMinimized ? 'open' : 'minimize') + '-' + (isHovered ? 'hover' : 'nohover');

  const navModetoPng = new Map([
    ['open-nohover', open],
    ['minimize-nohover', minimze],
    ['close-nohover', close],
    ['open-hover', openHover],
    ['minimize-hover', minimzeHover],
    ['close-hover', closeHover]
  ])

  const handleMouseEnter = () => {
    setIsHovered(true);
  };

  const handleMouseLeave = () => {
    setIsHovered(false);
  }

  const handleNavbarToggle = () => {
    setIsMinimized(!isMinimized);
  }

  return (
    <nav className={'navbar ' + (isMinimized ? 'minimized' : '')}>
      <div className='navHeader'>
        <div className='nav-control'>
          <button 
          onMouseEnter={handleMouseEnter} 
          onMouseLeave={handleMouseLeave}
          onClick={handleNavbarToggle}
          aria-controls={isMinimized ? "Expand navbar" : "Minimized navbar"}
          >
            <img src={navModetoPng.get(navbarImage)} alt='navbar control' />
          </button>
        </div>
        <div className='logo-name'>
          <img src={logo} alt='CareerCrack Logo' height="30px"/>
          {!isMinimized && <span>CareerCrack</span>}
        </div>
        <div className='search'>
          <FaSearch />
          <input type="text" onClick={() => isMinimized && handleNavbarToggle()} placeholder="Search" />
        </div>
      </div>
      <hr className="line-seperator" />
      <div className='links'>
        <Link to="/home" className={selectedTab === "/home" ? "selected" : ''}>
          <FaHome />
          {!isMinimized && <span>Home</span>}
        </Link>
        <Link to="/problems" className={selectedTab === "/problems" ? "selected" : ''}>
          <FaClipboardList />
          {!isMinimized && <span>Problems</span>}
        </Link>
        <Link to="/applications" className={selectedTab === "/applications" ? "selected" : ''}>
          <FaBriefcase />
          {!isMinimized && <span>Applications</span>}
        </Link>
        <Link to="/settings" className={selectedTab === "/settings" ? "selected" : ''}>
          <FaCog />
          {!isMinimized && <span>Settings</span>}
        </Link>
      </div>
    </nav>
  );
};