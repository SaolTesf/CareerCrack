import { React } from 'react';
import { Link } from 'react-router-dom';
import { FaHome, FaBriefcase, FaClipboardList, FaCog } from "react-icons/fa";
import './Navbar.css';

export const Navbar = () => {
  return (
    <nav className='navbar'>
      <Link to={"/home"}>
        <FaHome />
        <span>Home</span>
      </Link>
      <Link to={"/problems"}>
        <FaClipboardList />
        <span>Problems</span>
      </Link>
      <Link to={"/applications"}>
        <FaBriefcase />
        <span>Applications</span>
      </Link>
      <Link to={"/settings"}>
        <FaCog />
        <span>Settings</span>
      </Link>
    </nav>
  )
}