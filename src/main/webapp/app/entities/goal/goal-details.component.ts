import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import GoalService from './goal.service';
import { useDateFormat } from '@/shared/composables';
import { type IGoal } from '@/shared/model/goal.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'GoalDetails',
  setup() {
    const dateFormat = useDateFormat();
    const goalService = inject('goalService', () => new GoalService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const goal: Ref<IGoal> = ref({});

    const retrieveGoal = async goalId => {
      try {
        const res = await goalService().find(goalId);
        goal.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.goalId) {
      retrieveGoal(route.params.goalId);
    }

    return {
      ...dateFormat,
      alertService,
      goal,

      previousState,
    };
  },
});
