package com.kt_study.todo_alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.kt_study.todo_alarm.databinding.FragmentAlarmBinding

class AlarmFragment(
    private val initialHour: Int,
    private val initialMin: Int,
    private val timeSetListener: (hour: Int, min: Int) -> Unit,
) : DialogFragment() {

    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 현재 시간을 설정
        binding.tpAlarm.hour = initialHour
        binding.tpAlarm.minute = initialMin

        // 확인 버튼 클릭 이벤트
        binding.btnConfirm.setOnClickListener {
            val selectedHour = binding.tpAlarm.hour
            val selectedMinute = binding.tpAlarm.minute
            timeSetListener(selectedHour, selectedMinute)
            dismiss() // Fragment 닫기
        }

        // 취소 버튼 클릭 이벤트
        binding.btnCancel.setOnClickListener {
            dismiss() // Fragment 닫기
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
