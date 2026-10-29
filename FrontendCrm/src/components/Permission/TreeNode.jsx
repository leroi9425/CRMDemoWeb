import { useState } from "react";

// =========================================================================
// 1. CÂY QUYỀN ĐỆ QUY
// =========================================================================

export default function TreeNode({ node, checkedIds, handleCheck }) {
  const isChecked = checkedIds.includes(node.id);
  const hasChildren = node.children && node.children.length > 0;
  
  // State quản lý việc thu gọn / mở rộng các quyền con
  const [isExpanded, setIsExpanded] = useState(false);

  return (
    <div className="ml-6 mt-2">
      <div className="flex items-center">
        {/* Nút mũi tên Thu gọn / Mở rộng */}
        {hasChildren ? (
          <button
            type="button"
            onClick={() => setIsExpanded(!isExpanded)}
            className="w-5 h-5 flex items-center justify-center mr-1 text-slate-400 hover:text-slate-700 transition-colors focus:outline-none"
            title={isExpanded ? "Thu gọn" : "Mở rộng"}
          >
            <i className={`fa-solid fa-chevron-right text-xs transition-transform duration-200 ${isExpanded ? 'rotate-90 text-blue-600' : ''}`}></i>
          </button>
        ) : (
          <span className="w-5 mr-1"></span>
        )}

        {/* Checkbox và Tên quyền */}
        <label className={`flex items-center cursor-pointer whitespace-nowrap ${hasChildren ? 'font-bold' : 'font-normal'}`}>
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
      </div>

      {/* Chỉ hiển thị các node con khi có con VÀ đang mở (isExpanded) */}
      {hasChildren && isExpanded && (
        <div className="border-l border-dashed border-slate-300 pl-2 mt-1">
          {node.children.map(child => (
            <TreeNode key={child.id} node={child} checkedIds={checkedIds} handleCheck={handleCheck} />
          ))}
        </div>
      )}
    </div>
  );
}
