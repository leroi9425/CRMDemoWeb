import React, { useState, useEffect } from 'react';
import axiosInstance from '../api/axiosInstance';

// =========================================================================
// 1. CÂY QUYỀN ĐỆ QUY
// =========================================================================
const TreeNode = ({ node, checkedIds, handleCheck }) => {
  const isChecked = checkedIds.includes(node.id);
  const hasChildren = node.children && node.children.length > 0;

  return (
    <div className="ml-6 mt-2">
      <label className={`flex items-center cursor-pointer ${hasChildren ? 'font-bold' : 'font-normal'}`}>
        <input
          type="checkbox"
          checked={isChecked}
          onChange={(e) => handleCheck(node, e.target.checked)}
          className="mr-3 w-4 h-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500"
        />
        <span className="text-slate-800">{node.name}</span>
        {node.description && (
          <span className="text-slate-500 text-xs ml-2 font-normal">
            - {node.description}
          </span>
        )}
      </label>

      {hasChildren && (
        <div className="border-l border-dashed border-slate-300 pl-2 mt-1">
          {node.children.map(child => (
            <TreeNode key={child.id} node={child} checkedIds={checkedIds} handleCheck={handleCheck} />
          ))}
        </div>
      )}
    </div>
  );
};

// =========================================================================
// 2. MÀN HÌNH DANH SÁCH ROLE
// =========================================================================
const RoleList = ({ onEditRole, onCreateRole }) => {
  const [roles, setRoles] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchRoles();
  }, []);

  const fetchRoles = async () => {
    setLoading(true);
    try {
      const res = await axiosInstance.get('/roles');
      setRoles(res.data);
    } catch (error) {
      console.error("Lỗi lấy danh sách:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-sm border border-slate-200 overflow-hidden mt-6">
      <div className="px-6 py-4 border-b border-slate-200 flex justify-between items-center bg-slate-50">
        <h2 className="text-lg font-semibold text-slate-800">Nhóm quyền ({roles.length})</h2>
        <button 
          onClick={onCreateRole}
          className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded text-sm font-medium transition-colors flex items-center"
        >
          <i className="fa-solid fa-plus mr-2"></i> Thêm Mới
        </button>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-sm text-left">
          <thead className="text-xs text-slate-500 uppercase bg-slate-50 border-b">
            <tr>
              <th className="px-6 py-3 font-medium">HÀNH ĐỘNG</th>
              <th className="px-6 py-3 font-medium">NHÓM QUYỀN</th>
              <th className="px-6 py-3 font-medium">MÔ TẢ</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan="3" className="px-6 py-4 text-center text-slate-500">Đang tải...</td></tr>
            ) : roles.length === 0 ? (
              <tr><td colSpan="3" className="px-6 py-4 text-center text-slate-500">Chưa có dữ liệu</td></tr>
            ) : (
              roles.map(role => (
                <tr key={role.id} className="border-b hover:bg-slate-50">
                  <td className="px-6 py-4">
                    <button 
                      onClick={() => onEditRole(role)}
                      className="text-blue-600 hover:text-blue-800 font-medium flex items-center"
                    >
                      <i className="fa-regular fa-pen-to-square mr-1"></i> Sửa
                    </button>
                  </td>
                  <td className="px-6 py-4 font-semibold text-slate-800">{role.name}</td>
                  <td className="px-6 py-4 text-slate-600">{role.description || '...'}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

// =========================================================================
// 3. MÀN HÌNH THÊM / SỬA CÂY QUYỀN
// =========================================================================
const RoleEdit = ({ currentRole, onBack }) => {
  const isEditing = !!currentRole;
  const [formData, setFormData] = useState({ name: '', description: '' });
  const [treeData, setTreeData] = useState([]);
  const [checkedIds, setCheckedIds] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (isEditing) {
      setFormData({ name: currentRole.name, description: currentRole.description || '' });
    }
    fetchTreeAndPermissions();
  }, [currentRole]);

  const fetchTreeAndPermissions = async () => {
    setLoading(true);
    try {
      const treeRes = await axiosInstance.get('/permissions/tree');
      setTreeData(treeRes.data);

      if (isEditing) {
        const roleRes = await axiosInstance.get(`/roles/${currentRole.id}/permissions`);
        setCheckedIds(roleRes.data);
      }
    } catch (error) {
      console.error("Lỗi tải cây quyền:", error);
    } finally {
      setLoading(false);
    }
  };

  const getAllIds = (node, ids = []) => {
    ids.push(node.id);
    if (node.children) node.children.forEach(child => getAllIds(child, ids));
    return ids;
  };

  const handleCheck = (node, isChecked) => {
    const familyIds = getAllIds(node);
    setCheckedIds(prev => {
      if (isChecked) {
        return Array.from(new Set([...prev, ...familyIds]));
      } else {
        return prev.filter(id => !familyIds.includes(id));
      }
    });
  };

  const handleSave = async () => {
    try {
      let savedRole;
      if (isEditing) {
        const res = await axiosInstance.put(`/roles/${currentRole.id}`, formData);
        savedRole = res.data;
      } else {
        const res = await axiosInstance.post(`/roles`, formData);
        savedRole = res.data;
      }

      await axiosInstance.post(`/roles/${savedRole.id}/permissions`, {
        permissionIds: checkedIds
      });

      alert("Cập nhật thành công!");
      onBack();
    } catch (error) {
      console.error("Lỗi khi lưu:", error);
      alert("Có lỗi xảy ra khi lưu! " + (error.response?.data?.message || ""));
    }
  };

  if (loading) return <div className="p-6 text-center text-slate-500">Đang tải dữ liệu...</div>;

  return (
    <div className="mt-6">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-bold text-slate-800">
          {isEditing ? `Sửa nhóm quyền` : 'Thêm mới nhóm quyền'}
        </h2>
        <div className="space-x-3">
          <button onClick={handleSave} className="bg-green-600 hover:bg-green-700 text-white px-5 py-2 rounded text-sm font-medium transition-colors">
            Cập nhật
          </button>
          <button onClick={onBack} className="bg-slate-500 hover:bg-slate-600 text-white px-5 py-2 rounded text-sm font-medium transition-colors">
            Trở về
          </button>
        </div>
      </div>

      <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-6 mb-6">
        <h3 className="text-lg font-semibold text-slate-800 mb-4 border-b pb-2">Thông tin Nhóm quyền</h3>
        <div className="space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center">
            <label className="w-48 font-medium text-slate-700">Nhóm quyền <span className="text-red-500">*</span></label>
            <input 
              className="flex-1 border border-slate-300 rounded px-3 py-2 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
              value={formData.name} 
              onChange={e => setFormData({...formData, name: e.target.value})}
              placeholder="Nhập tên nhóm quyền..."
            />
          </div>
          <div className="flex flex-col sm:flex-row">
            <label className="w-48 font-medium text-slate-700 mt-2">Mô tả</label>
            <textarea 
              className="flex-1 border border-slate-300 rounded px-3 py-2 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 min-h-[100px]"
              value={formData.description} 
              onChange={e => setFormData({...formData, description: e.target.value})}
              placeholder="Nhập mô tả cho nhóm quyền..."
            />
          </div>
        </div>
      </div>

      <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-6">
        <h3 className="text-lg font-semibold text-slate-800 mb-4 border-b pb-2">Chọn quyền</h3>
        <div className="bg-slate-50 p-6 rounded border border-slate-200 h-96 overflow-y-auto">
          {treeData.map(rootNode => (
            <TreeNode key={rootNode.id} node={rootNode} checkedIds={checkedIds} handleCheck={handleCheck} />
          ))}
        </div>
      </div>
    </div>
  );
};

// =========================================================================
// 4. MAIN COMPONENT (XUẤT RA NGOÀI DÙNG Ở APP.JSX)
// =========================================================================
export default function RoleManagerTab() {
  const [currentView, setCurrentView] = useState('LIST');
  const [selectedRole, setSelectedRole] = useState(null);

  const handleEditRole = (role) => {
    setSelectedRole(role);
    setCurrentView('EDIT');
  };

  const handleCreateRole = () => {
    setSelectedRole(null);
    setCurrentView('EDIT');
  };

  return (
    <main className="flex-grow max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full">
      {currentView === 'LIST' && (
        <RoleList onEditRole={handleEditRole} onCreateRole={handleCreateRole} />
      )}
      {currentView === 'EDIT' && (
        <RoleEdit currentRole={selectedRole} onBack={() => setCurrentView('LIST')} />
      )}
    </main>
  );
}
