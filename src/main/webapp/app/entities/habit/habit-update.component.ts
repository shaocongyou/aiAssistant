import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import HabitService from './habit.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import { Habit, type IHabit } from '@/shared/model/habit.model';
import { HabitType } from '@/shared/model/enumerations/habit-type.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'HabitUpdate',
  setup() {
    const habitService = inject('habitService', () => new HabitService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const habit: Ref<IHabit> = ref(new Habit());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);
    const habitTypeValues: Ref<string[]> = ref(Object.keys(HabitType));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'zh-cn'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveHabit = async habitId => {
      try {
        const res = await habitService().find(habitId);
        habit.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.habitId) {
      retrieveHabit(route.params.habitId);
    }

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required('本字段不能为空.'),
        maxLength: validations.maxLength('本字段最大长度为 100 个字符.', 100),
      },
      habitType: {
        required: validations.required('本字段不能为空.'),
      },
      frequency: {
        required: validations.required('本字段不能为空.'),
      },
      startDate: {},
      active: {
        required: validations.required('本字段不能为空.'),
      },
      user: {},
    };
    const v$ = useVuelidate(validationRules, habit as any);
    v$.value.$validate();

    return {
      habitService,
      alertService,
      habit,
      previousState,
      habitTypeValues,
      isSaving,
      currentLanguage,
      users,
      v$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.habit.id) {
        this.habitService()
          .update(this.habit)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A Habit is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.habitService()
          .create(this.habit)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A Habit is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
