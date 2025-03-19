import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import HabitService from './habit.service';
import { type IHabit } from '@/shared/model/habit.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'HabitDetails',
  setup() {
    const habitService = inject('habitService', () => new HabitService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const habit: Ref<IHabit> = ref({});

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

    return {
      alertService,
      habit,

      previousState,
    };
  },
});
